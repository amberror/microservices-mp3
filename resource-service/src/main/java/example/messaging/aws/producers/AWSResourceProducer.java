package example.messaging.aws.producers;

import example.messaging.common.producers.ResourceProducer;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component("AWSResourceProducer")
@Slf4j
public class AWSResourceProducer implements ResourceProducer {

	@Resource
	private SqsTemplate sqsTemplate;

	@Value("${cloud.aws.sqs.resource.queue.name}")
	private String queueName;

	@Override
	public void sendMessage(Long resourceId) {
		sqsTemplate.send(queueName, MessageBuilder
				.withPayload(resourceId)
				.setHeader("key", resourceId)
				.build());
		log.info("[AWS-RESOURCE-PRODUCER] message sent [{}]", resourceId);
	}



	public void sendMessage(String queueName, String message) {
		sqsTemplate.send(queueName, message);
		System.out.println("Message sent to queue: " + queueName);
	}
}
