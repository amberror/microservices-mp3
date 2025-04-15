package example.services.impl;

import example.constants.ResourceConstants;
import example.enums.FileMetadata;
import example.models.FileMetadataModel;
import example.services.FileMetadataService;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class FileMetadataServiceImpl implements FileMetadataService {

	@Override
	public Optional<FileMetadataModel> getFileMetadata(byte[] fileBytes) {
		Optional<FileMetadataModel> result;
		try (TikaInputStream tikaIS = TikaInputStream.get(fileBytes)) {
			Tika tika = new Tika();
			Metadata metadata = new Metadata();
			tika.parse(tikaIS, metadata);
			result = Optional.of(this.populateMetadata(metadata));
		} catch (Exception e) {
			result = Optional.empty();
		}
		return result;
	}

	private FileMetadataModel populateMetadata(Metadata metadata) {
		FileMetadataModel metadataModel = new FileMetadataModel();
		metadataModel.setName(this.getMetadataProperty(metadata, FileMetadata.NAME.getIdentifier()));
		metadataModel.setAlbum(this.getMetadataProperty(metadata, FileMetadata.ALBUM.getIdentifier()));
		metadataModel.setArtist(this.getMetadataProperty(metadata, FileMetadata.ARTIST.getIdentifier()));
		metadataModel.setDuration(this.convertDurationToMMSS(this.getMetadataProperty(metadata, FileMetadata.DURATION.getIdentifier())));
		metadataModel.setYear(Integer.valueOf(this.getMetadataProperty(metadata, FileMetadata.YEAR.getIdentifier())));
		return metadataModel;
	}

	private String getMetadataProperty(Metadata metadata, String identifier) {
		return Optional.ofNullable(metadata.get(identifier)).orElse(ResourceConstants.UNKNOWN);
	}

	private String convertDurationToMMSS(String durationInMilliseconds) {
		try {
			double duration = Double.parseDouble(durationInMilliseconds);
			long minutes = (long) duration / 60;
			long seconds = (long) duration % 60;
			return String.format(ResourceConstants.MM_SS_TEMPLATE, minutes, seconds);
		} catch (NumberFormatException e) {
			return ResourceConstants.UNKNOWN;
		}
	}
}
