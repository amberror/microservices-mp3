package example.integration;

import example.config.FeignDefaultClientConfig;
import example.dto.SongRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient(name = "song-service", configuration = FeignDefaultClientConfig.class)
public interface SongFeignClient {

	@PostMapping("/songs")
	ResponseEntity<Map<String, Long>> saveMetadata(@RequestBody SongRequestDTO dto);

}
