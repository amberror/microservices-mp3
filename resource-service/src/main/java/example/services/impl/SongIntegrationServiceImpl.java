package example.services.impl;

import example.constants.ResourceConstants;
import example.dto.integration.SongBatchRequestDTO;
import example.dto.integration.SongRequestDTO;
import example.integration.SongFeignClient;
import example.services.SongIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SongIntegrationServiceImpl implements SongIntegrationService {

	@Resource
	private SongFeignClient client;

	@Override
	public boolean saveMetadata(SongRequestDTO dto) {
		ResponseEntity<Map<String, Long>> response = client.saveMetadata(dto);
		return response.getStatusCode().is2xxSuccessful() && response.getBody().containsValue(dto.getId());
	}

	@Override
	public boolean deleteMetadata(SongBatchRequestDTO dto) {
		String ids = dto.getIds().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(ResourceConstants.COMMA_SEPARATOR));
		return client.deleteMetadata(ids, dto).getStatusCode().is2xxSuccessful();
	}
}
