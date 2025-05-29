package example.services;

import example.dto.integration.StorageResponseDTO;


public interface S3Service {
	String uploadFile(String bucketName, String filePath, String key, byte[] fileBytes);
	String uploadFile(String bucketName, String filePath, byte[] fileBytes);
	String uploadFile(StorageResponseDTO storageData, byte[] fileBytes);
	byte[] readFile(String bucketName, String filePath, String key);
	byte[] readFile(StorageResponseDTO storageData, String key);
	boolean updateFile(String bucketName, String filePath, String key, byte[] fileBytes);
	boolean deleteFile(String bucketName, String filePath, String key);
	boolean deleteFile(StorageResponseDTO storageData, String key);
	String moveFileFromStageToPermanent(StorageResponseDTO stageData, StorageResponseDTO permanentData, String key);
}
