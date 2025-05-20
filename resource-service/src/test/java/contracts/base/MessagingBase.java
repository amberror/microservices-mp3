package contracts.base;

import contracts.config.ContractStubConfig;
import example.ResourceServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = {ResourceServiceApplication.class, ContractStubConfig.class}
)
@Testcontainers
@AutoConfigureMessageVerifier
public class MessagingBase {

	@Autowired
	private KafkaTemplate<Long, Long> resourceKafkaTemplate;

	@Value("${kafka.producer.resource.topic.name}")
	private String testResourceTopicName;

	@Container
	static KafkaContainer kafka = new KafkaContainer();

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
		registry.add("kafka.bootstrap.servers", kafka::getBootstrapServers);
		registry.add("kafka.producer.resource.topic.name", () -> "resource-topic-test");
	}

	public void fileCreatedEvent(int id) {
		resourceKafkaTemplate.send(testResourceTopicName, (long) id, (long) id);
	}

}