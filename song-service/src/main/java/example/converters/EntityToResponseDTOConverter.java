package example.converters;

import example.dto.SongResponseDTO;
import example.entities.SongEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class EntityToResponseDTOConverter implements Converter<SongEntity, SongResponseDTO> {

	@Override
	public SongResponseDTO convert(SongEntity source) {
		return SongResponseDTO.builder()
				.id(source.getId())
				.name(source.getName())
				.album(source.getAlbum())
				.artist(source.getArtist())
				.duration(source.getDuration())
				.year(source.getYear())
				.build();
	}
}
