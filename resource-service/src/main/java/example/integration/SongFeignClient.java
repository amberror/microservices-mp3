package example.integration;

import example.config.FeignDefaultClientConfig;
import example.dto.integration.SongBatchRequestDTO;
import example.dto.integration.SongRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@FeignClient(
		name = "song-service",
		configuration = FeignDefaultClientConfig.class
)
public interface SongFeignClient {

	@PostMapping("/songs")
	ResponseEntity<Map<String, Long>> saveMetadata(@RequestBody SongRequestDTO dto);

	@DeleteMapping("/songs")
	ResponseEntity<Map<String, List<Long>>> deleteMetadata(@RequestParam("id") String ids, @RequestBody SongBatchRequestDTO dto);
}
