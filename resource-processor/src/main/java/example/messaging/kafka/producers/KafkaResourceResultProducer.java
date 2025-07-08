package example.messaging.kafka.producers;

import example.messaging.common.producers.ResourceResultProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component("kafkaResourceResultProducer")
@Slf4j
public class KafkaResourceResultProducer implements ResourceResultProducer {
	@Autowired
	private KafkaTemplate<Long, Long> resourceResultKafkaTemplate;

	@Value("${kafka.producer.resource.result.topic.name}")
	private String resourceTopicName;

	public void sendMessage(Long resourceId) {
		resourceResultKafkaTemplate.send(resourceTopicName, resourceId, resourceId);
		log.info("[KAFKA-RESOURCE-RESULT-PRODUCER] message sent [{}]", resourceId);
	}
}
