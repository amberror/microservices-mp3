package example.converters;

import example.dto.ResourceDTO;
import example.entities.ResourceEntity;
import example.services.S3Service;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class EntityToDTOConverter implements Converter<ResourceEntity, ResourceDTO> {

	@Resource
	private S3Service s3Service;

	@Value("${resource.s3.bucket.name}")
	private String resourceBucketName;

	@Override
	public ResourceDTO convert(ResourceEntity source) {
		return ResourceDTO.builder()
				.id(source.getId())
				.data(s3Service.readFile(resourceBucketName, source.getFileIdentifier()))
				.build();
	}

}
