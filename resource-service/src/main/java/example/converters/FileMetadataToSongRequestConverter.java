package example.converters;

import example.dto.SongRequestDTO;
import example.models.FileMetadataModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class FileMetadataToSongRequestConverter implements Converter<FileMetadataModel, SongRequestDTO> {

	@Override
	public SongRequestDTO convert(FileMetadataModel source) {
		return SongRequestDTO.builder()
				.album(source.getAlbum())
				.artist(source.getArtist())
				.duration(source.getDuration())
				.name(source.getName())
				.year(source.getYear())
				.build();
	}

}
