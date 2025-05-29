package example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
@Slf4j
public class KafkaConfig {

	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;

	@Bean
	public ConsumerFactory<Long, Long> resourceConsumerFactory(
			@Value("${kafka.resource.consumer.group.id}") String consumerGroupId,
			@Value("${kafka.bootstrap.servers}") String bootstrapServers,
			@Value("${kafka.resource.consumer.auto.offset.reset}") String offsetReset
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
	public ConcurrentKafkaListenerContainerFactory<Long, Long> resourceKafkaListenerContainerFactory(ConsumerFactory<Long, Long> resourceConsumerFactory) {
		return new ConcurrentKafkaListenerContainerFactory<>() {{
			setConsumerFactory(resourceConsumerFactory);
			getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
		}};
	}


	@Bean
	public ProducerFactory<Long, Long> resourceResultProducerFactory(
			@Value("${kafka.producer.resource-result.acks}") String acks,
			@Value("${kafka.producer.resource-result.reties}") String reties,
			@Value("${kafka.producer.resource-result.maxInFlight}") String maxInFlight,
			@Value("${kafka.producer.resource-result.allRetriesTimeout}") String allRetriesTimeout,
			@Value("${kafka.producer.resource-result.timeoutPerRequest}") String timeoutPerRequest,
			@Value("${kafka.producer.resource-result.idempotence}") String idempotence
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
	public KafkaTemplate<Long, Long> resourceResultKafkaTemplate(ProducerFactory<Long, Long> resourceResultProducerFactory) {
		return new KafkaTemplate<>(resourceResultProducerFactory);
	}

	@Bean
	public KafkaAdmin admin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic resourceProcessorTopic(
			@Value("${kafka.producer.resource-result.topic.name}") String resourceTopicName,
			@Value("${kafka.resource-result.replication.factor}") Integer replicas,
			@Value("${kafka.resource-result.partition.count}") Integer partitions
	) {
		return TopicBuilder.name(resourceTopicName)
				.partitions(partitions)
				.replicas(replicas)
				.compact()
				.build();
	}
}
