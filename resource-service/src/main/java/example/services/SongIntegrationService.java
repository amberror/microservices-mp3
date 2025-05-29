package example.services;


import example.dto.integration.SongBatchRequestDTO;
import example.dto.integration.SongRequestDTO;


public interface SongIntegrationService {
	boolean saveMetadata(SongRequestDTO dto);
	boolean deleteMetadata(SongBatchRequestDTO dto);
}
