package example.converters;

import example.dto.ResourceBatchDTO;
import example.dto.integration.SongBatchRequestDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class SongToResourceBatchDTOConverter implements Converter<SongBatchRequestDTO, ResourceBatchDTO> {

	@Override
	public ResourceBatchDTO convert(SongBatchRequestDTO source) {
		return ResourceBatchDTO.builder().ids(source.getIds()).build();
	}
}
