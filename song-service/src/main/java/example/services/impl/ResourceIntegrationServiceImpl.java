package example.services.impl;

import example.services.ResourceFeignClient;
import example.services.ResourceIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class ResourceIntegrationServiceImpl implements ResourceIntegrationService {

	@Resource
	private ResourceFeignClient client;

	@Override
	public boolean isResourceExist(Long id) {
		return client.isResourceExist(id);
	}

}
