package example.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;

	@Bean
	@Lazy
	public ProducerFactory<Long, Long> resourceProducerFactory(
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
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		}});
	}

	@Bean
	@Lazy
	public KafkaTemplate<Long, Long> resourceKafkaTemplate(ProducerFactory<Long, Long> resourceProducerFactory) {
		KafkaTemplate<Long, Long> template = new KafkaTemplate<>(resourceProducerFactory);
		template.setObservationEnabled(Boolean.TRUE);
		return template;
	}

	@Bean
	@Lazy
	public KafkaAdmin admin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return new KafkaAdmin(configs);
	}

	@Bean
	@Lazy
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

	@Bean
	@Lazy
	public ConsumerFactory<Long, Long> resourceResultConsumerFactory(
			@Value("${kafka.resource-result.consumer.group.id}") String consumerGroupId,
			@Value("${kafka.bootstrap.servers}") String bootstrapServers,
			@Value("${kafka.resource-result.consumer.auto.offset.reset}") String offsetReset
	) {
		return new DefaultKafkaConsumerFactory<>(new HashMap<>() {{
			put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
			put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
			put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.TRUE);
			put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
			put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
		}});
	}

	@Bean
	@Lazy
	public ConcurrentKafkaListenerContainerFactory<Long, Long> resourceResultKafkaListenerContainerFactory(ConsumerFactory<Long, Long> resourceResultConsumerFactory) {
		return new ConcurrentKafkaListenerContainerFactory<>() {{
			setConsumerFactory(resourceResultConsumerFactory);
			getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
			getContainerProperties().setObservationEnabled(Boolean.TRUE);
		}};
	}


}
