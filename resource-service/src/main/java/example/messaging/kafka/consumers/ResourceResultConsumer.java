package example.messaging.kafka.consumers;

import example.messaging.kafka.services.MessagingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ResourceResultConsumer {

	@Resource
	private MessagingService messagingService;

	@RetryableTopic(
			attempts = "2",
			backoff = @Backoff(delay = 3000),
			dltStrategy = DltStrategy.FAIL_ON_ERROR,
			autoStartDltHandler = "false"
	)
	@KafkaListener(
			topics = "${kafka.consumer.resource-result.topic.name}",
			groupId = "${kafka.resource-result.consumer.group.id}",
			containerFactory = "resourceResultKafkaListenerContainerFactory"
	)
	public void consumeMessage(ConsumerRecord<Long, Long> record) {
		log.info("[RESOURCE-RESULT-CONSUMER] message received [{}]", record.key());
		messagingService.processResourceResult(record.value());
	}

	@DltHandler
	public void consumeMessageDlt(ConsumerRecord<Long, Long> record) {
		log.info("[DEAD-LETTER-RESOURCE-RESULT-CONSUMER] message received [{}]", record.toString());
		try {
			this.consumeMessage(record);
		} catch (Exception e) {
			log.error("[DEAD-LETTER-ERROR-{}] {}", record.topic(), e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}
