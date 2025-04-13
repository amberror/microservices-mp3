package example.services;


import example.dto.ResourceBatchDTO;
import example.dto.SongRequestDTO;


public interface SongIntegrationService {
	boolean saveMetadata(SongRequestDTO dto);
	boolean deleteMetadata(ResourceBatchDTO dto);
}
