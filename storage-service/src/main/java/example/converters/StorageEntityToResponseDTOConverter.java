package example.converters;

import example.dto.StorageResponseDTO;
import example.entities.StorageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class StorageEntityToResponseDTOConverter implements Converter<StorageEntity, StorageResponseDTO> {

	@Override
	public StorageResponseDTO convert(StorageEntity source) {
		return StorageResponseDTO.builder()
				.id(source.getId())
				.storageType(source.getStorageType().toString())
				.bucket(source.getBucket())
				.path(source.getPath())
				.build();
	}

}

