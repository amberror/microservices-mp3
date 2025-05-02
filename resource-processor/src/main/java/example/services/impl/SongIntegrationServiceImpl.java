package example.services.impl;

import example.dto.SongRequestDTO;
import example.integration.SongFeignClient;
import example.services.SongIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class SongIntegrationServiceImpl implements SongIntegrationService {

	@Resource
	private SongFeignClient client;

	@Override
	public boolean saveMetadata(SongRequestDTO dto) {
		ResponseEntity<Map<String, Long>> response = client.saveMetadata(dto);
		return response.getStatusCode().is2xxSuccessful() && response.getBody().containsValue(dto.getId());
	}
}
