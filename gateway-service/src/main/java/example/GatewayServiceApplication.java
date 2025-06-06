package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;


@SpringBootApplication
public class GatewayServiceApplication {
	//TODO: create config service based on spring config, for dynamic properties changes
	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(GatewayServiceApplication.class, args);
	}
}
