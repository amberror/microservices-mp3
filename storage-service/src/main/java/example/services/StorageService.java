package example.services;

import example.dto.CreateStorageDTO;
import example.dto.StorageResponseDTO;

import java.util.List;


public interface StorageService {
	List<StorageResponseDTO> getStorages();
	Long saveStorage(CreateStorageDTO dto);
	List<Long> deleteStorages(String ids);
}
