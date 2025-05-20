package contracts.config;

import contracts.utils.KafkaMessageVerifier;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;


@EnableKafka
@TestConfiguration
@PropertySource(value = "classpath:application-stub.properties")
public class ContractStubConfig {

	@Bean
	public KafkaMessageVerifier kafkaTemplateMessageVerifier() {
		return new KafkaMessageVerifier();
	}

	@Bean
	public ConsumerFactory<Long, Long> testResourceConsumerFactory(
			@Value("${kafka.resource.consumer.group.id}") String consumerGroupId,
			@Value("${kafka.bootstrap.servers}") String bootstrapServers
	) {
		return new DefaultKafkaConsumerFactory<>(new HashMap<>() {{
			put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
			put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.TRUE);
			put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
			put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
		}});
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<Long, Long> testResourceKafkaListenerContainerFactory(ConsumerFactory<Long, Long> testResourceConsumerFactory) {
		return new ConcurrentKafkaListenerContainerFactory<>() {{
			setConsumerFactory(testResourceConsumerFactory);
		}};
	}

}



