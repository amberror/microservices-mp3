package example.services;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "resource-service")
public interface ResourceFeignClient {

	@GetMapping("/resources/exist/{id}")
	boolean isResourceExist(@PathVariable("id") Long id);
}
