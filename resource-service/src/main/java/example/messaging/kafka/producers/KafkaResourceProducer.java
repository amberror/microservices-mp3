package example.messaging.kafka.producers;

import example.messaging.common.producers.ResourceProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component("kafkaResourceProducer")
@Slf4j
public class KafkaResourceProducer implements ResourceProducer {
	@Autowired
	private KafkaTemplate<Long, Long> resourceKafkaTemplate;

	@Value("${kafka.producer.resource.topic.name}")
	private String resourceTopicName;

	@Override
	public void sendMessage(Long resourceId) {
		resourceKafkaTemplate.send(resourceTopicName, resourceId, resourceId);
		log.info("[KAFKA-RESOURCE-PRODUCER] message sent [{}]", resourceId);
	}
}
