package example.converters;

import example.dto.CreateStorageDTO;
import example.entities.StorageEntity;
import example.enums.StorageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class CreateStorageDTOToEntityConverter implements Converter<CreateStorageDTO, StorageEntity> {

	@Override
	public StorageEntity convert(CreateStorageDTO source) {
		return StorageEntity.builder()
				.storageType(StorageType.valueOf(source.getStorageType()))
				.bucket(source.getBucket())
				.path(source.getPath())
				.build();
	}

}
