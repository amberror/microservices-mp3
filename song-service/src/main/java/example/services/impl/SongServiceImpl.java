package example.services.impl;

import example.constants.SongConstants;
import example.dto.SongRequestDTO;
import example.dto.SongResponseDTO;
import example.entities.SongEntity;
import example.repositories.SongRepository;
import example.services.ConstraintsService;
import example.services.ResourceIntegrationService;
import example.services.SongService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
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

	@Resource
	private ConstraintsService constraintsService;

	@Override
	public SongResponseDTO getSong(Long id) {
		constraintsService.checkIdConstraints(id);
		SongEntity entity = songRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format(SongConstants.ENTITY_NOT_FOUND_MESSAGE_TEMPLATE, id)));
		return conversionService.convert(entity, SongResponseDTO.class);
	}

	@Override
	public Long saveSong(SongRequestDTO dto) {
		if(songRepository.existsById(dto.getId())) {
			throw new EntityExistsException(String.format(SongConstants.ENTITY_ALREADY_EXISTS_MESSAGE_TEMPLATE, dto.getId()));
		}
		SongEntity entity = songRepository.save(conversionService.convert(dto, SongEntity.class));
		return entity.getId();
	}

	@Override
	public List<Long> deleteSongs(String ids) {
		List<Long> existedIds = !StringUtils.isBlank(ids) ? this.parseStringCommaSeparated(ids) : List.of();
		songRepository.deleteAllById(existedIds);
		return existedIds;
	}

	private List<Long> parseStringCommaSeparated(String value) {
		constraintsService.checkInlineStringIdsConstraints(value);
		return Arrays.stream(value.split(SongConstants.COMMA_SEPARATOR))
				.map(id -> this.mapStringToLong(id, String.format(SongConstants.INVALID_ID_FORMAT_MESSAGE_TEMPLATE, id)))
				.filter(id -> songRepository.existsById(id))
				.toList();
	}

	private Long mapStringToLong(String value, String errorMessage) {
		Long result;
		try {
			result = Long.parseLong(value);
			constraintsService.checkIdConstraints(result);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(errorMessage);
		}
		return result;
	}
}
