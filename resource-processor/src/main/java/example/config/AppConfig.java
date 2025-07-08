package example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.messaging.aws.consumers.AWSResourceConsumer;
import example.messaging.aws.producers.AWSResourceResultProducer;
import example.messaging.common.MessagingServiceType;
import example.messaging.common.consumers.ResourceConsumer;
import example.messaging.common.producers.ResourceResultProducer;
import example.messaging.kafka.consumers.KafkaResourceConsumer;
import example.messaging.kafka.producers.KafkaResourceResultProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;


@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

	@Value("${messaging.service.type}")
	private String messagingServiceType;

	@Autowired
	private Set<Converter> customConverters;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		customConverters.forEach(registry::addConverter);
	}

	@Bean
	public ResourceResultProducer resourceResultProducer() {
		return switch (MessagingServiceType.getByLabel(messagingServiceType.toLowerCase())) {
			case KAFKA -> new KafkaResourceResultProducer();
			case AWS -> new AWSResourceResultProducer();
			default -> throw new IllegalArgumentException("Invalid resource result producer service type: " + messagingServiceType);
		};
	}

	@Bean
	public ResourceConsumer resourceConsumer() {
		return switch (MessagingServiceType.getByLabel(messagingServiceType.toLowerCase())) {
			case KAFKA -> new KafkaResourceConsumer();
			case AWS -> new AWSResourceConsumer();
			default -> throw new IllegalArgumentException("Invalid resource consumer service type: " + messagingServiceType);
		};
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
