package example.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;

	@Bean
	public ProducerFactory<String, Long> resourceProducerFactory(
			@Value("${kafka.producer.resource.acks}") String acks,
			@Value("${kafka.producer.resource.reties}") String reties,
			@Value("${kafka.producer.resource.maxInFlight}") String maxInFlight,
			@Value("${kafka.producer.resource.allRetriesTimeout}") String allRetriesTimeout,
			@Value("${kafka.producer.resource.timeoutPerRequest}") String timeoutPerRequest,
			@Value("${kafka.producer.resource.idempotence}") String idempotence
	) {
		return new DefaultKafkaProducerFactory<>(new HashMap<>() {{
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			put(ProducerConfig.ACKS_CONFIG, acks);
			put(ProducerConfig.RETRIES_CONFIG, reties);
			put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlight);
			put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, allRetriesTimeout);
			put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, timeoutPerRequest);
			put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		}});
	}

	@Bean
	public KafkaTemplate<String, Long> resourceKafkaTemplate(ProducerFactory<String, Long> resourceProducerFactory) {
		return new KafkaTemplate<>(resourceProducerFactory);
	}

	@Bean
	public KafkaAdmin admin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic resourceProcessorTopic(
			@Value("${kafka.producer.resource.topic.name}") String resourceTopicName,
			@Value("${kafka.resource.replication.factor}") Integer replicas,
			@Value("${kafka.resource.partition.count}") Integer partitions
	) {
		return TopicBuilder.name(resourceTopicName)
				.partitions(partitions)
				.replicas(replicas)
				.compact()
				.build();
	}

}
