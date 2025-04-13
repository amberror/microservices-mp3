package example.converters;

import example.dto.SongRequestDTO;
import example.entities.SongEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class RequestDTOToEntityConverter implements Converter<SongRequestDTO, SongEntity> {
	@Override
	public SongEntity convert(SongRequestDTO source) {
		return SongEntity.builder()
				.id(source.getId())
				.name(source.getName())
				.album(source.getAlbum())
				.artist(source.getArtist())
				.duration(source.getDuration())
				.year(Integer.valueOf(source.getYear()))
				.build();
	}
}
