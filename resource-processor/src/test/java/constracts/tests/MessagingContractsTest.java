package constracts.tests;

import constracts.config.ContractTestConfig;
import example.ResourceProcessorApplication;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = {ContractTestConfig.class, ResourceProcessorApplication.class}
)
@AutoConfigureStubRunner(
		ids = "example:resource-service",
		stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Testcontainers
public class MessagingContractsTest {
	@Container
	private static KafkaContainer kafka = new KafkaContainer();

	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
		registry.add("kafka.bootstrap.servers", kafka::getBootstrapServers);
	}

	@Autowired
	private StubTrigger trigger;

	@Value("${kafka.producer.resource.topic.name}")
	private String testResourceTopicName;

	@Value("${kafka.resource.consumer.group.id}")
	private String groupId;


	@Test
	public void contextLoads() {
		long expectedID = 1L;
		long expectedValue = 1L;
		this.trigger.trigger("resource-created");
		Awaitility.await().untilAsserted(() -> this.verifyKafkaMessage(testResourceTopicName, expectedID, expectedValue));
	}

	private void verifyKafkaMessage(String topicName, Long expectedKey, Long expectedValue) {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), groupId, "true");
		consumerProps.putAll(new HashMap<>() {{
			put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
			put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
		}});
		try (KafkaConsumer<Long, Long> consumer = new KafkaConsumer<>(consumerProps)) {
			consumer.subscribe(Collections.singletonList(topicName));

			ConsumerRecord<Long, Long> record = KafkaTestUtils.getSingleRecord(consumer, topicName);
			Assertions.assertEquals(expectedKey, record.key());
			Assertions.assertEquals(expectedValue, record.value());
		}
	}

}



