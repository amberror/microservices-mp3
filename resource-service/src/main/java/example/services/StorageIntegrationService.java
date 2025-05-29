package example.services;

import example.dto.integration.StorageResponseDTO;
import example.enums.StorageType;

import java.util.List;


public interface StorageIntegrationService {
	List<StorageResponseDTO> getStorages();
	StorageResponseDTO getStorageByType(StorageType storageType);
}
