package example.services;

import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.enums.StorageType;


public interface ResourceService {
	ResourceDTO getFile(Long id);
	boolean checkExist(Long id);
	Long saveFileStage(byte[] fileBytes);
	ResourceBatchDTO deleteFiles(String ids);
	byte[] getFileDataFromStorage(Long id, StorageType storageType);
}
