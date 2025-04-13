package example.services;

import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;


public interface ResourceService {
	ResourceDTO getFile(Long id);
	boolean checkExist(Long id);
	ResourceDTO saveFile(byte[] fileBytes);
	ResourceBatchDTO deleteFiles(String ids);
}
