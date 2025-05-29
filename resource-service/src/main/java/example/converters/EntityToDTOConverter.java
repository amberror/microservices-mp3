package example.converters;

import example.dto.ResourceDTO;
import example.entities.ResourceEntity;
import example.enums.StorageType;
import example.services.S3Service;
import example.services.StorageIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class EntityToDTOConverter implements Converter<ResourceEntity, ResourceDTO> {

	@Resource
	private S3Service s3Service;

	@Resource
	private StorageIntegrationService storageIntegrationService;

	@Override
	public ResourceDTO convert(ResourceEntity source) {
		return ResourceDTO.builder()
				.id(source.getId())
				.data(s3Service.readFile(storageIntegrationService.getStorageByType(StorageType.PERMANENT), source.getFileIdentifier()))
				.build();
	}

}
