package example.messaging.kafka.consumers;

import example.messaging.common.consumers.ResourceConsumer;
import example.messaging.common.services.MessagingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;


@Component("kafkaResourceConsumer")
@Slf4j
public class KafkaResourceConsumer implements ResourceConsumer {

	@Resource
	private MessagingService messagingService;

	@RetryableTopic(
			attempts = "2",
			backoff = @Backoff(delay = 3000),
			dltStrategy = DltStrategy.FAIL_ON_ERROR,
			autoStartDltHandler = "false"
	)
	@KafkaListener(
			topics = "${kafka.consumer.resource.topic.name}",
			groupId = "${kafka.resource.consumer.group.id}",
			containerFactory = "resourceKafkaListenerContainerFactory"
	)
	public void consumeMessageHandler(ConsumerRecord<Long, Long> record) {
		this.consumeMessage(record.key(), record.value(), record.topic());
	}

	@DltHandler
	public void consumeMessageDltHandler(ConsumerRecord<Long, Long> record) {
		this.consumeMessageDlt(record.key(), record.value(), record.topic());
	}

	@Override
	public void consumeMessage(Long key, Long message, String topic) {
		log.info("[KAFKA-RESOURCE-CONSUMER] message received [{}]", key);
		messagingService.processResourceEvent(message);
	}

	@Override
	public void consumeMessageDlt(Long key, Long message, String topic) {
		log.info("[KAFKA-DEAD-LETTER-RESOURCE-CONSUMER] message received key : [{}], message : [{}]", key, message);
		try {
			this.consumeMessage(key, message, topic);
		} catch (Exception e) {
			log.error("[KAFKA-DEAD-LETTER-ERROR-{}] {}", topic, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
