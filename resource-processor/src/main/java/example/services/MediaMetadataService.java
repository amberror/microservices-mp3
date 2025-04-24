package example.services;

import example.models.FileMetadataModel;

import java.util.Optional;


public interface MediaMetadataService {
	Optional<FileMetadataModel> getFileMetadata(byte[] fileBytes);
}
