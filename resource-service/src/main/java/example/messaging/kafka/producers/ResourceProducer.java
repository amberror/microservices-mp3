package example.messaging.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ResourceProducer {
	@Autowired
	private KafkaTemplate<Long, Long> resourceKafkaTemplate;

	@Value("${kafka.producer.resource.topic.name}")
	private String resourceTopicName;

	public void sendMessage(Long resourceId) {
		resourceKafkaTemplate.send(resourceTopicName, resourceId, resourceId);
		log.info("[RESOURCE-PRODUCER] message sent [{}]", resourceId);
	}
}
