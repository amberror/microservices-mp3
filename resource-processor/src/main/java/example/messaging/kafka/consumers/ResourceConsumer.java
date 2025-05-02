package example.messaging.kafka.consumers;

import example.messaging.kafka.services.MessagingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ResourceConsumer {

	@Resource
	private MessagingService messagingService;

	@RetryableTopic(
			attempts = "3",
			backoff = @Backoff(delay = 2000),
			autoCreateTopics = "false"
	)
	@KafkaListener(
			topics = "${kafka.consumer.resource.topic.name}",
			groupId = "${kafka.resource.consumer.group.id}",
			containerFactory = "resourceKafkaListenerContainerFactory"
	)
	public void consumeMessage(final @Payload Long resourceId) {
		log.info("[RESOURCE-CONSUMER] message received [{}]", resourceId);
		messagingService.processResourceEvent(resourceId);
	}

}
