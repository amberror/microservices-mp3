package example.config;

import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignDefaultClientConfig {

	//exponential interval increasing
	@Bean
	public Retryer retryer(
			@Value("${feign.default.client.config.period}") Integer period,
			@Value("${feign.default.client.config.maxPeriod}") Integer maxPeriod,
			@Value("${feign.default.client.config.maxAttempts}") Integer maxAttempts
	) {
		return new Retryer.Default(period, maxPeriod, maxAttempts);
	}
}
