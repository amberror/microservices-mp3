package example.services.impl;

import example.services.S3Service;
import example.utils.HashUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Optional;


@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

	@Resource
	private S3Client s3Client;

	@Override
	public boolean createBucket(String bucketName) {
		boolean result = true;
		try {
			s3Client.createBucket(CreateBucketRequest.builder()
					.bucket(bucketName)
					.build());
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
		}
		return result;
	}

	@Override
	public Optional<String> uploadFile(String bucketName, String key, byte[] fileBytes) {
		Optional<String> fileNameResult = Optional.of(key);
		try {
			s3Client.putObject(PutObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.build(), RequestBody.fromBytes(fileBytes));
		} catch (Exception e) {
			log.error(e.getMessage());
			fileNameResult = Optional.empty();
		}
		return fileNameResult;
	}

	@Override
	public Optional<String> uploadFile(String bucketName, byte[] fileBytes) {
		if(fileBytes == null) {
			return Optional.empty();
		}
		return this.uploadFile(bucketName, HashUtils.generateNameFromBytes(fileBytes), fileBytes);
	}

	@Override
	public byte[] readFile(String bucketName, String key) {
		byte[] result;
		try {
			ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.build());
			result = response.readAllBytes();
		} catch (Exception e) {
			log.error(e.getMessage());
			result = new byte[0];
		}
		return result;
	}

	@Override
	public Optional<String> updateFile(String bucketName, String key, byte[] fileBytes) {
		return this.uploadFile(bucketName, key, fileBytes);
	}

	@Override
	public boolean deleteFile(String bucketName, String key) {
		boolean result = true;
		try {
			s3Client.deleteObject(DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.build());
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
		}
		return result;
	}

}
