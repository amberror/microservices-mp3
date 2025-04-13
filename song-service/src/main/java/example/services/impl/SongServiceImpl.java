package example.services.impl;

import example.constants.SongConstants;
import example.dto.SongRequestDTO;
import example.dto.SongResponseDTO;
import example.entities.SongEntity;
import example.repositories.SongRepository;
import example.services.ResourceIntegrationService;
import example.services.SongService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class SongServiceImpl implements SongService {

	@Resource
	private SongRepository songRepository;

	@Resource
	private ResourceIntegrationService resourceIntegrationService;

	@Resource
	private ConversionService conversionService;

	@Override
	public SongResponseDTO getSong(Long id) {
		SongEntity entity = songRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Song metadata with id " + id + " doest not exist"));
		return conversionService.convert(entity, SongResponseDTO.class);
	}

	@Override
	public Long saveSong(SongRequestDTO dto) {
		/*if(!resourceIntegrationService.isResourceExist(dto.getId())) {
			throw new EntityNotFoundException("Resource with ID " + dto.getId() + " does not exist");
		}*/
		if(songRepository.existsById(dto.getId())) {
			throw new EntityExistsException("Song metadata with ID " + dto.getId() + " already exist");
		}
		SongEntity entity = songRepository.save(conversionService.convert(dto, SongEntity.class));
		return entity.getId();
	}

	@Override
	public List<Long> deleteSongs(String ids) {
		List<Long> existedIds = ids != null && !ids.isBlank() ?
				this.parseStringCommaSeparated(ids, "ID should be valid integer value") :
				List.of();
		songRepository.deleteAllById(existedIds);
		return existedIds;
	}

	private List<Long> parseStringCommaSeparated(String value, String errorMessage) {
		return Arrays.stream(value.split(SongConstants.COMMA_SEPARATOR))
				.map(id -> this.mapStringToLong(id, errorMessage))
				.filter(id -> songRepository.existsById(id))
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
