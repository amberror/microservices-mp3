package example.services.impl;

import example.services.ResourceIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ResourceIntegrationServiceImpl implements ResourceIntegrationService {

	@Resource
	private RestTemplate resourceRestTemplate;

	@Value("${integration.resources.base.url}")
	private String baseUrl;

	@Value("${integration.resources.get.exist.url}")
	private String getExistPath;

	@Override
	public boolean isResourceExist(Long id) {
		return resourceRestTemplate.getForEntity(baseUrl + getExistPath, String.class, id).getStatusCode().is2xxSuccessful();
	}

}
