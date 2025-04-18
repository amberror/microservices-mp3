package example.services.impl;

import example.constants.ResourceConstants;
import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.dto.SongRequestDTO;
import example.entities.ResourceEntity;
import example.exceptions.InvalidArgumentException;
import example.models.FileMetadataModel;
import example.repositories.ResourceRepository;
import example.services.ConstraintsService;
import example.services.FileMetadataService;
import example.services.ResourceService;
import example.services.SongIntegrationService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
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

	@Resource
	private ConstraintsService constraintsService;

	@Override
	public ResourceDTO getFile(Long id) {
		constraintsService.checkIdConstraints(id);
		ResourceEntity entity = resourceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format(ResourceConstants.RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE, id)));
		return conversionService.convert(entity, ResourceDTO.class);
	}

	@Override
	public boolean checkExist(Long id) {
		constraintsService.checkIdConstraints(id);
		if(!resourceRepository.existsById(id)) {
			throw new EntityNotFoundException(String.format(ResourceConstants.RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE, id));
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
		List<Long> existedIds = !StringUtils.isBlank(ids) ?
				this.parseStringCommaSeparated(ids) :
				List.of();
		resourceRepository.deleteAllById(existedIds);
		ResourceBatchDTO dto = ResourceBatchDTO.builder().ids(existedIds).build();
		if(!songIntegrationService.deleteMetadata(dto)) {
			throw new RestClientException("Failed to delete resource's metadata in song service");
		}
		return dto;
	}


	private List<Long> parseStringCommaSeparated(String value) {
		constraintsService.checkInlineStringIdsConstraints(value);
		return Arrays.stream(value.split(ResourceConstants.COMMA_SEPARATOR))
				.map(id -> this.mapStringToLong(id, String.format(ResourceConstants.INVALID_ID_FORMAT_MESSAGE_TEMPLATE, id)))
				.filter(id -> resourceRepository.existsById(id))
				.toList();
	}

	private Long mapStringToLong(String value, String errorMessage) {
		Long result;
		try {
			result = Long.parseLong(value);
			constraintsService.checkIdConstraints(result);
		} catch (NumberFormatException e) {
			throw new InvalidArgumentException(errorMessage);
		}
		return result;
	}
}
