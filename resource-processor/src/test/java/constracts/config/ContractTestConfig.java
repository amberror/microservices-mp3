package constracts.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;


@TestConfiguration
@PropertySource("classpath:application-contract.properties")
public class ContractTestConfig {

	@Bean
	public MessageVerifierSender<Message<?>> standaloneMessageVerifier(
			@Value("${kafka.producer.resource.acks}") String acks,
			@Value("${kafka.producer.resource.reties}") String reties,
			@Value("${kafka.producer.resource.maxInFlight}") String maxInFlight,
			@Value("${kafka.producer.resource.allRetriesTimeout}") String allRetriesTimeout,
			@Value("${kafka.producer.resource.timeoutPerRequest}") String timeoutPerRequest,
			@Value("${kafka.bootstrap.servers}") String bootstrapServers,
			@Value("${kafka.producer.resource.idempotence}") String idempotence
	) {

		ProducerFactory<Long, Long> testResourceProducerFactory = new DefaultKafkaProducerFactory<>(new HashMap<>() {{
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			put(ProducerConfig.ACKS_CONFIG, acks);
			put(ProducerConfig.RETRIES_CONFIG, reties);
			put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlight);
			put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, allRetriesTimeout);
			put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, timeoutPerRequest);
			put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		}});

		return new MessageVerifierSender<>() {
			@Override
			public void send(Message<?> message, String destination, @Nullable YamlContract contract) {
			}

			@Override
			public <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
				Map<String, Object> newHeaders = headers != null ? new HashMap<>(headers) : new HashMap<>();
				newHeaders.put(KafkaHeaders.TOPIC, destination);
				KafkaTemplate<Long, Long> kafkaTemplate = new KafkaTemplate<>(testResourceProducerFactory);
				if(payload instanceof String) {
					kafkaTemplate.send(MessageBuilder.createMessage(Long.valueOf((String) payload), new MessageHeaders(newHeaders)));
				} else {
					kafkaTemplate.send(MessageBuilder.createMessage(payload, new MessageHeaders(newHeaders)));
				}
			}
		};
	}
}