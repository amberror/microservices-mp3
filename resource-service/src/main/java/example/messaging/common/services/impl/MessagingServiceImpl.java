package example.messaging.common.services.impl;

import example.constants.ResourceConstants;
import example.dto.integration.StorageResponseDTO;
import example.entities.ResourceEntity;
import example.enums.StorageType;
import example.messaging.common.services.MessagingService;
import example.repositories.ResourceRepository;
import example.services.S3Service;
import example.services.StorageIntegrationService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MessagingServiceImpl implements MessagingService {

	@Resource
	private ResourceRepository resourceRepository;

	@Resource
	private StorageIntegrationService storageIntegrationService;

	@Resource
	private S3Service s3Service;

	@Override
	public void processResourceResult(Long id) {
		ResourceEntity entity = resourceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format(ResourceConstants.RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE, id)));
		StorageResponseDTO stagingStorage = storageIntegrationService.getStorageByType(StorageType.STAGING);
		StorageResponseDTO permanentStorage = storageIntegrationService.getStorageByType(StorageType.PERMANENT);
		String permanentFileIdentifier = s3Service.moveFileFromStageToPermanent(stagingStorage, permanentStorage, entity.getFileIdentifier());
		entity.setFileIdentifier(permanentFileIdentifier);
		resourceRepository.save(entity);
	}

}