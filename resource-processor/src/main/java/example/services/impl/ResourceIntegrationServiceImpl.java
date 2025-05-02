package example.services.impl;

import example.integration.ResourceFeignClient;
import example.services.ResourceIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class ResourceIntegrationServiceImpl implements ResourceIntegrationService {

	@Resource
	private ResourceFeignClient resourceFeignClient;

	@Override
	public byte[] getResource(Long id) {
		return resourceFeignClient.getResource(id);
	}

}
