package example.services.impl;

import example.dto.integration.StorageResponseDTO;
import example.enums.StorageType;
import example.integration.StorageFeignClient;
import example.services.StorageIntegrationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class StorageIntegrationServiceImpl implements StorageIntegrationService {

	@Resource
	private StorageFeignClient client;

	@Value("${fallback.staging.bucket.name}")
	private String stagingBucketNameFallback;

	@Value("${fallback.staging.bucket.path}")
	private String stagingBucketPathFallback;

	@Value("${fallback.permanent.bucket.name}")
	private String permanentBucketNameFallback;

	@Value("${fallback.permanent.bucket.path}")
	private String permanentBucketPathFallback;

	@Override
	@CircuitBreaker(name = "storageIntegrationCB", fallbackMethod = "getStoragesFallback")
	public List<StorageResponseDTO> getStorages() {
		return client.getStorages().getBody();
	}

	private List<StorageResponseDTO> getStoragesFallback(Exception e) {
		log.error("[GET-STORAGE-FALLBACK] fallback invoked", e);
		return List.of(
				StorageResponseDTO.builder()
						.id(1L)
						.storageType(StorageType.STAGING.toString())
						.bucket(stagingBucketNameFallback)
						.path(stagingBucketPathFallback)
						.build(),
				StorageResponseDTO.builder()
						.id(2L)
						.storageType(StorageType.PERMANENT.toString())
						.bucket(permanentBucketNameFallback)
						.path(permanentBucketPathFallback)
						.build()
		);
	}

	@Override
	public StorageResponseDTO getStorageByType(StorageType storageType) {
		return this.getStorages().stream()
				.filter(storage -> storageType.toString().equals(storage.getStorageType()))
				.findAny()
				.orElseThrow(() -> new RuntimeException("Should be processed as fault tolerance"));
	}

}
