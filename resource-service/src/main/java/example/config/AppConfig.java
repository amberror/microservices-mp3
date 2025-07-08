package example.config;

import example.constants.ResourceConstants;
import example.messaging.aws.consumers.AWSResourceResultConsumer;
import example.enums.MessagingServiceType;
import example.messaging.aws.producers.AWSResourceProducer;
import example.messaging.common.consumers.ResourceResultConsumer;
import example.messaging.kafka.consumers.KafkaResourceResultConsumer;
import example.messaging.kafka.producers.KafkaResourceProducer;
import example.messaging.common.producers.ResourceProducer;
import example.services.SecretsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;


@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

	@Value("${messaging.service.type}")
	private String messagingServiceType;

	@Value("${cloud.aws.secrets.manager.resource.db.credentials.arn}")
	private String resourceDBARN;

	@Autowired
	private Set<Converter> customConverters;

	@Autowired
	private SecretsManagerService secretsManagerService;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		customConverters.forEach(registry::addConverter);
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Bean
	public ResourceProducer resourceProducer() {
		return switch (MessagingServiceType.getByLabel(messagingServiceType.toLowerCase())) {
			case KAFKA -> new KafkaResourceProducer();
			case AWS -> new AWSResourceProducer();
			default -> throw new IllegalArgumentException(String.format(ResourceConstants.INVALID_MESSAGE_PRODUCER_SERVICE_TYPE, messagingServiceType));
		};
	}

	@Bean
	public ResourceResultConsumer resourceResultConsumer() {
		return switch (MessagingServiceType.getByLabel(messagingServiceType.toLowerCase())) {
			case KAFKA -> new KafkaResourceResultConsumer();
			case AWS -> new AWSResourceResultConsumer();
			default -> throw new IllegalArgumentException(String.format(ResourceConstants.INVALID_MESSAGE_CONSUMER_SERVICE_TYPE, messagingServiceType));
		};
	}

	@Bean
	public DataSource dataSource() {
		Map<String, String> secrets = secretsManagerService.getSecretAsMap(resourceDBARN);

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(secrets.get(ResourceConstants.DATASOURCE_DRIVER_SECRET_NAME));
		dataSource.setUrl(secrets.get(ResourceConstants.DATASOURCE_URL_SECRET_NAME));
		dataSource.setUsername(secrets.get(ResourceConstants.DATASOURCE_USERNAME_SECRET_NAME));
		dataSource.setPassword(secrets.get(ResourceConstants.DATASOURCE_PASSWORD_SECRET_NAME));

		return dataSource;
	}
}
