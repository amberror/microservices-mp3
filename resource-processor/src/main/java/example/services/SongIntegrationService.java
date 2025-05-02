package example.services;

import example.dto.SongRequestDTO;


public interface SongIntegrationService {
	boolean saveMetadata(SongRequestDTO dto);
}
