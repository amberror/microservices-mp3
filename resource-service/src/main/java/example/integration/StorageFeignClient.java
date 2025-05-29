package example.integration;

import example.config.FeignDefaultClientConfig;
import example.dto.integration.StorageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(
		name = "storage-service",
		configuration = FeignDefaultClientConfig.class
)
public interface StorageFeignClient {

	@GetMapping("/storages")
	ResponseEntity<List<StorageResponseDTO>> getStorages();

}
