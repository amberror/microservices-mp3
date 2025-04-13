package example.services.impl;

import example.constants.ResourceConstants;
import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.dto.SongRequestDTO;
import example.entities.ResourceEntity;
import example.models.FileMetadataModel;
import example.repositories.ResourceRepository;
import example.services.FileMetadataService;
import example.services.ResourceService;
import example.services.SongIntegrationService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;


@Service
public class ResourceServiceImpl implements ResourceService {

	@Resource
	private ResourceRepository resourceRepository;

	@Resource
	private SongIntegrationService songIntegrationService;

	@Resource
	private ConversionService conversionService;

	@Resource
	private FileMetadataService fileMetadataService;

	@Override
	public ResourceDTO getFile(Long id) {
		ResourceEntity entity = resourceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Resource with id " + id + " doest not exist"));
		return conversionService.convert(entity, ResourceDTO.class);
	}

	@Override
	public boolean checkExist(Long id) {
		if(!resourceRepository.existsById(id)) {
			throw new EntityNotFoundException("Resource with id " + id + " does not exist");
		}
		return true;
	}

	@Override
	@Transactional
	public ResourceDTO saveFile(byte[] fileBytes) {
		ResourceEntity entity = resourceRepository.save(ResourceEntity.builder().data(fileBytes).build());
		FileMetadataModel fileMetadata = fileMetadataService.getFileMetadata(fileBytes)
				.orElseThrow(() -> new IllegalArgumentException("Resource's metadata is not valid"));

		SongRequestDTO requestDTO = conversionService.convert(fileMetadata, SongRequestDTO.class);
		requestDTO.setId(entity.getId());

		if(!songIntegrationService.saveMetadata(requestDTO)) {
			throw new RestClientException("Failed to save resource's metadata in song service");
		}
		return conversionService.convert(entity, ResourceDTO.class);
	}

	@Override
	@Transactional
	public ResourceBatchDTO deleteFiles(String ids) {
		List<Long> existedIds = ids != null && !ids.isBlank() ?
				this.parseStringCommaSeparated(ids, "ID should be valid integer value") :
				List.of();
		resourceRepository.deleteAllById(existedIds);
		ResourceBatchDTO dto = ResourceBatchDTO.builder().ids(existedIds).build();
		if(!songIntegrationService.deleteMetadata(dto)) {
			throw new RestClientException("Failed to delete resource's metadata in song service");
		}
		return dto;
	}


	private List<Long> parseStringCommaSeparated(String value, String errorMessage) {
		return Arrays.stream(value.split(ResourceConstants.COMMA_SEPARATOR))
				.map(id -> this.mapStringToLong(id, errorMessage))
				.filter(id -> resourceRepository.existsById(id))
				.toList();
	}

	private Long mapStringToLong(String value, String errorMessage) {
		Long result;
		try {
			result = Long.parseLong(value);
		} catch (Exception e) {
			throw new IllegalArgumentException(errorMessage);
		}
		return result;
	}
}
