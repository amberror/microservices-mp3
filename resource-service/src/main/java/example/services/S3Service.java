package example.services;

import java.util.Optional;


public interface S3Service {
	boolean createBucket(String bucketName);

	Optional<String> uploadFile(String bucketName, String key, byte[] fileBytes);

	Optional<String> uploadFile(String bucketName, byte[] fileBytes);

	byte[] readFile(String bucketName, String key);

	Optional<String> updateFile(String bucketName, String key, byte[] fileBytes);

	boolean deleteFile(String bucketName, String key);
}
