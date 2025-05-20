package contracts.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.PropertySource;


@TestConfiguration
@PropertySource(value = "classpath:application-stub.properties")
public class ContractStubConfig {

}
