package example.services;


import example.dto.SongRequestDTO;
import example.dto.SongResponseDTO;

import java.util.List;


public interface SongService {
	SongResponseDTO getSong(Long id);
	Long saveSong(SongRequestDTO dto);
	List<Long> deleteSongs(String ids);
}
