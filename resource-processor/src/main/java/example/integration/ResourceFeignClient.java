package example.integration;


import example.config.FeignDefaultClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "resource-service", configuration = FeignDefaultClientConfig.class)
public interface ResourceFeignClient {
	@GetMapping("/resources/{id}")
	byte[] getResource(@PathVariable("id") Long id);
}
