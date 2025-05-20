package integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;


@TestConfiguration
@PropertySource(value = "classpath:application-integration.properties")
public class IntegrationTestsConfig {

}
