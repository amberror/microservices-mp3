package example.services;

import example.enums.StorageType;


public interface ResourceIntegrationService {

	byte[] getResource(Long id);
	byte[] getResourceWithStorage(Long id, StorageType storageType);
}
