package example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;


@EnableKafka
@Configuration
@Slf4j
public class KafkaConfig {

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

}
