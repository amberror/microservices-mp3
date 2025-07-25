package example.messaging.aws.consumers;

import example.messaging.common.consumers.ResourceConsumer;
import example.messaging.common.services.MessagingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Component("AWSResourceConsumer")
@Slf4j
public class AWSResourceConsumer implements ResourceConsumer {

	@Resource
	private MessagingService messagingService;

	@SqsListener("${cloud.aws.sqs.resource.queue.name}")
	public void consumeMessageHandler(Message<Long> message, @Value("${cloud.aws.sqs.resource.queue.name}") String queueName) {
		this.consumeMessage((Long) message.getHeaders().get("key"), message.getPayload(), queueName);
	}

	@SqsListener("${cloud.aws.sqs.resource.dlq.name}")
	public void consumeMessageDltHandler(Message<Long> message, @Value("${cloud.aws.sqs.resource.dlq.name}") String queueName) {
		this.consumeMessageDlt((Long) message.getHeaders().get("key"), message.getPayload(), queueName);
	}

	@Override
	public void consumeMessage(Long key, Long message, String topic) {
		log.info("[AWS-RESOURCE-CONSUMER] message received [{}]", key);
		messagingService.processResourceEvent(message);
	}

	@Override
	public void consumeMessageDlt(Long key, Long message, String topic) {
		log.info("[AWS-DEAD-LETTER-RESOURCE-CONSUMER] message received key : [{}], message : [{}]", key, message);
		try {
			this.consumeMessage(key, message, topic);
		} catch (Exception e) {
			log.error("[AWS-DEAD-LETTER-ERROR-{}] {}", topic, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
