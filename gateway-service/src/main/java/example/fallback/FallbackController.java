package example.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

	private static final Logger LOG = LoggerFactory.getLogger(FallbackController.class);

	@RequestMapping("/fallback/resources")
	public Mono<ResponseEntity<String>> resourceServiceFallback() {
		LOG.info("[RESOURCE-FALLBACK]");
		return Mono.just(
				ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body("Resource Service is currently unavailable. Please try again later.")
		);
	}

	@RequestMapping("/fallback/songs")
	public Mono<ResponseEntity<String>> songServiceFallback() {
		LOG.info("[SONG-FALLBACK]");
		return Mono.just(
				ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body("Song Service is currently unavailable. Please try again later.")
		);
	}

	@RequestMapping("/fallback/storages")
	public Mono<ResponseEntity<String>> storageServiceFallback() {
		LOG.info("[STORAGE-FALLBACK]");
		return Mono.just(
				ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body("Storage Service is currently unavailable. Please try again later.")
		);
	}

	@RequestMapping("/fallback/auth")
	public Mono<ResponseEntity<String>> authServiceFallback() {
		LOG.info("[AUTH-FALLBACK]");
		return Mono.just(
				ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body("Authentication Service is currently unavailable. Please try again later.")
		);
	}


}
