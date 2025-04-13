package example.services.impl;

import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.dto.SongRequestDTO;
import example.services.SongIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SongIntegrationServiceImpl implements SongIntegrationService {

	@Resource
	private RestTemplate songRestTemplate;

	@Value("${integration.songs.base.url}")
	private String baseUrl;

	@Value("${integration.songs.post.url}")
	private String postPath;

	@Value("${integration.songs.delete.url}")
	private String deletePath;

	@Override
	public boolean saveMetadata(SongRequestDTO dto) {
		ResponseEntity<Map<String, Long>> response = songRestTemplate.exchange(
				baseUrl + postPath,
				HttpMethod.POST,
				new HttpEntity<>(dto),
				new ParameterizedTypeReference<>() {}
		);
		return response.getStatusCode().is2xxSuccessful() &&
				response.getBody().containsValue(dto.getId());
	}

	@Override
	public boolean deleteMetadata(ResourceBatchDTO dto) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity<Map<String, List<Long>>> response = songRestTemplate.exchange(
				baseUrl + postPath + "?id=" + dto.getIds().stream().map(String::valueOf).collect(Collectors.joining(",")),
				HttpMethod.DELETE,
				new HttpEntity<>(dto),
				new ParameterizedTypeReference<>() {}
		);
		return response.getStatusCode().is2xxSuccessful();
	}

}
