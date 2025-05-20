package e2e.config;

import e2e.utils.WireMockServerWrapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class E2ETestsConfig {
	@Bean
	public WireMockServerWrapper wireMockServerWrapper() {
		return new WireMockServerWrapper();
	}
}
