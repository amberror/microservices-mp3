package example.services.impl;

import example.constants.ResourceConstants;
import example.dto.integration.StorageResponseDTO;
import example.exceptions.ResourceSaveException;
import example.services.S3Service;
import example.utils.HashUtils;
import jakarta.annotation.Resource;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	public String uploadFile(String bucketName, String filePath, @NotNull String key, @NotNull byte[] fileBytes) {
		Optional<String> fileNameResult = Optional.of(key);
		try {
			s3Client.putObject(PutObjectRequest.builder()
					.bucket(bucketName)
					.key(this.safeConcat(filePath, key))
					.build(), RequestBody.fromBytes(fileBytes));
		} catch (Exception e) {
			log.error(e.getMessage());
			fileNameResult = Optional.empty();
		}
		return fileNameResult.orElseThrow(() ->
				new ResourceSaveException(String.format(ResourceConstants.SAVE_S3_FAILED_MESSAGE_TEMPLATE, bucketName)));
	}

	@Override
	public String uploadFile(String bucketName, String filePath, @NotNull byte[] fileBytes) {
		return this.uploadFile(bucketName, filePath, HashUtils.generateNameFromBytes(fileBytes), fileBytes);
	}

	@Override
	public String uploadFile(StorageResponseDTO storageData, @NotNull byte[] fileBytes) {
		return this.uploadFile(storageData.getBucket(), storageData.getPath(),
				HashUtils.generateNameFromBytes(fileBytes), fileBytes);
	}

	@Override
	public byte[] readFile(String bucketName, String filePath, @NotNull String key) {
		byte[] result;
		try {
			ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder()
					.bucket(bucketName)
					.key(this.safeConcat(filePath, key))
					.build());
			result = response.readAllBytes();
		} catch (Exception e) {
			log.error(e.getMessage());
			result = new byte[0];
		}
		return result;
	}

	@Override
	public byte[] readFile(StorageResponseDTO storageData, @NotNull String key) {
		return this.readFile(storageData.getBucket(), storageData.getPath(), key);
	}

	@Override
	public boolean updateFile(String bucketName, String filePath, @NotNull String key, @NotNull byte[] fileBytes) {
		boolean result;
		try {
			result = this.uploadFile(bucketName, filePath, key, fileBytes) != null;
		} catch (Exception e) {
			log.error("Can not update file in bucket", e);
			result = false;
		}
		return result;
	}

	@Override
	public boolean deleteFile(String bucketName, String filePath, @NotNull String key) {
		boolean result = true;
		try {
			s3Client.deleteObject(DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(this.safeConcat(filePath, key))
					.build());
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
		}
		return result;
	}

	@Override
	public boolean deleteFile(StorageResponseDTO storageData, @NotNull String key) {
		return this.deleteFile(storageData.getBucket(), storageData.getPath(), key);
	}

	@Override
	@Transactional
	public String moveFileFromStageToPermanent(StorageResponseDTO stageData, StorageResponseDTO permanentData, @NotNull String key) {
		byte[] fileData = this.readFile(stageData, key);
		if(!this.deleteFile(stageData, key)) {
			throw new ResourceSaveException(String.format(ResourceConstants.STAGING_STORAGE_DELETION_FAILED_MESSAGE_TEMPLATE, stageData, key));
		}
		return this.uploadFile(permanentData, fileData);
	}

	private String safeConcat(String str1, String str2) {
		return Optional.ofNullable(str1).orElse(StringUtils.EMPTY) + Optional.ofNullable(str2).orElse(StringUtils.EMPTY);
	}
}
