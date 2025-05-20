package e2e.config;

import e2e.utils.WireMockServerWrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.ConfigurableEnvironment;


@TestConfiguration
public class WireMockConfig {
	private final WireMockServerWrapper wireMockServerWrapper;

	private final ConfigurableEnvironment environment;

	private static final String LOCALHOST = "localhost:";

	@Autowired
	public WireMockConfig(WireMockServerWrapper wireMockServerWrapper, ConfigurableEnvironment environment) {
		this.wireMockServerWrapper = wireMockServerWrapper;
		this.environment = environment;
	}

	@PostConstruct
	public void setWireMockPort() {
		environment.getSystemProperties().put("song-service.integration.default.url", LOCALHOST + wireMockServerWrapper.getPort());
	}
}
