package example.services.impl;

import example.annotations.processors.ValidCsvIdsProcessor;
import example.constants.StorageServiceConstants;
import example.dto.CreateStorageDTO;
import example.dto.StorageResponseDTO;
import example.entities.StorageEntity;
import example.repositories.StorageRepository;
import example.services.StorageConversionService;
import example.services.StorageService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.micrometer.common.util.StringUtils.isBlank;


@Service
public class StorageServiceImpl implements StorageService {

	@Resource
	private StorageRepository storageRepository;

	@Resource
	private StorageConversionService conversionService;

	@Resource
	private ValidCsvIdsProcessor validCsvIdsProcessor;

	@Override
	public List<StorageResponseDTO> getStorages() {
		return conversionService.convertAll(storageRepository.findAll(), StorageResponseDTO.class);
	}

	@Override
	public Long saveStorage(CreateStorageDTO dto) {
		if(storageRepository.existsByBucketAndPath(dto.getBucket(), dto.getPath())) {
			throw new EntityExistsException(
					String.format(StorageServiceConstants.ENTITY_ALREADY_EXISTS_MESSAGE_TEMPLATE,
							dto.getBucket(), dto.getPath()));
		}
		StorageEntity entity = storageRepository.save(conversionService.convert(dto, StorageEntity.class));
		return entity.getId();
	}

	@Override
	public List<Long> deleteStorages(String ids) {
		if(isBlank(ids)) {
			return List.of();
		}
		List<Long> existedIds = validCsvIdsProcessor.parseStringCommaSeparated(ids).stream()
				.filter(id -> storageRepository.existsById(id))
				.toList();
		storageRepository.deleteAllById(existedIds);
		return existedIds;
	}

}
