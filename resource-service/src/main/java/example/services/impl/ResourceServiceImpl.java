package example.services.impl;

import example.constants.ResourceConstants;
import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.dto.integration.SongBatchRequestDTO;
import example.dto.integration.StorageResponseDTO;
import example.entities.ResourceEntity;
import example.enums.StorageType;
import example.exceptions.InvalidArgumentException;
import example.messaging.common.producers.ResourceProducer;
import example.repositories.ResourceRepository;
import example.services.*;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
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
	private S3Service s3Service;

	@Resource
	private ConstraintsService constraintsService;

	@Resource
	private ResourceProducer resourceProducer;

	@Resource
	private StorageIntegrationService storageIntegrationService;

	@Override
	public ResourceDTO getFile(Long id) {
		return conversionService.convert(this.getResourceEntity(id), ResourceDTO.class);
	}

	@Override
	public byte[] getFileDataFromStorage(Long id, StorageType storageType) {
		return s3Service.readFile(storageIntegrationService.getStorageByType(storageType),
				this.getResourceEntity(id).getFileIdentifier());
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
	public Long saveFileStage(byte[] fileBytes) {
		StorageResponseDTO stagingStorage = storageIntegrationService.getStorageByType(StorageType.STAGING);
		String fileIdentifier = s3Service.uploadFile(stagingStorage, fileBytes);
		ResourceEntity entity = resourceRepository.save(ResourceEntity.builder().fileIdentifier(fileIdentifier).build());
		resourceProducer.sendMessage(entity.getId());
		return entity.getId();
	}

	@Override
	public ResourceBatchDTO deleteFiles(String ids) {
		List<Long> existedIds = !StringUtils.isBlank(ids) ?
				this.parseStringCommaSeparated(ids) :
				List.of();
		StorageResponseDTO permanentStorage = storageIntegrationService.getStorageByType(StorageType.PERMANENT);
		resourceRepository.findAllById(existedIds)
				.forEach(entity -> s3Service.deleteFile(permanentStorage, entity.getFileIdentifier()));
		resourceRepository.deleteAllById(existedIds);
		SongBatchRequestDTO dto = SongBatchRequestDTO.builder().ids(existedIds).build();
		//TODO : make kafka deletion event
		if(!songIntegrationService.deleteMetadata(dto)) {
			throw new RestClientException(ResourceConstants.REST_DELETE_FILE_FAILED_MESSAGE);
		}
		return conversionService.convert(dto, ResourceBatchDTO.class);
	}

	private ResourceEntity getResourceEntity(Long id) {
		constraintsService.checkIdConstraints(id);
		return resourceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format(ResourceConstants.RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE, id)));
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
