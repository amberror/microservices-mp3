package example.services;

import example.models.FileMetadataModel;

import java.util.Optional;


public interface FileMetadataService {
	Optional<FileMetadataModel> getFileMetadata(byte[] fileBytes);
}
