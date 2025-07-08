package example.messaging.aws.producers;

import example.messaging.common.producers.ResourceResultProducer;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component("AWSResourceResultProducer")
@Slf4j
public class AWSResourceResultProducer implements ResourceResultProducer {
	@Resource
	private SqsTemplate sqsTemplate;

	@Value("${cloud.aws.sqs.resource.result.queue.name}")
	private String queueName;

	@Override
	public void sendMessage(Long resourceId) {
		log.info("Resource Result Queue : {}", queueName);
		sqsTemplate.send(queueName, MessageBuilder
				.withPayload(resourceId)
				.setHeader("key", resourceId)
				.build());
		log.info("[AWS-RESOURCE-RESULT-PRODUCER] message sent [{}]", resourceId);
	}
}
