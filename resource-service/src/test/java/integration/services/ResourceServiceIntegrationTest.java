package integration.services;

import example.ResourceServiceApplication;
import example.dto.ResourceDTO;
import example.entities.ResourceEntity;
import example.messaging.kafka.producers.ResourceProducer;
import example.repositories.ResourceRepository;
import example.services.ConstraintsService;
import example.services.S3Service;
import example.services.SongIntegrationService;
import example.services.impl.ResourceServiceImpl;
import example.utils.HashUtils;
import integration.config.IntegrationTestsConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest(
		classes = {ResourceServiceApplication.class, IntegrationTestsConfig.class},
		webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@Testcontainers
class ResourceServiceIntegrationTest {

	@Autowired
	private ResourceServiceImpl resourceService;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private SongIntegrationService songIntegrationService;

	@Autowired
	private ResourceProducer resourceProducer;

	@Autowired
	private ConstraintsService constraintsService;

	@Value("${resource.s3.bucket.name}")
	private String testResourceBucketName;

	@Value("${kafka.producer.resource.topic.name}")
	private String testResourceTopicName;

	@Value("classpath:files/test-file.mp3")
	private Resource resourceFile;

	private static final Integer ZOOKEEPER_PORT = 2181;
	private static final Integer KAFKA_PORT_OUTSIDE = 9093;
	private static final Integer KAFKA_PORT_INTERNAL = 9092;
	private static final Integer LOCALSTACK_PORT = 4566;
	private static final String KAFKA_ALIAS = "kafka";
	private static final String ZOOKEEPER_ALIAS = "zookeeper";
	private static final String LOCALHOST = "localhost";
	private static final String ZOOKEEPER_URL_INTERNAL = ZOOKEEPER_ALIAS + ":" + ZOOKEEPER_PORT;
	private static final String KAFKA_URL_INTERNAL = KAFKA_ALIAS + ":" + KAFKA_PORT_INTERNAL;
	private static final String KAFKA_URL_OUTSIDE = LOCALHOST + ":" + KAFKA_PORT_OUTSIDE;


	private static final Network NETWORK = Network.newNetwork();

	@Container
	private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17-alpine3.21")
			.withDatabaseName("resource-db-test")
			.withUsername("postgres-test")
			.withPassword("postgres-test")
			.withInitScript("init-scripts/resource-db/init.sql");

	@Container
	private static final GenericContainer<?> zookeeperContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper:latest"))
			.withNetwork(NETWORK)
			.withExposedPorts(ZOOKEEPER_PORT)
			.withNetworkAliases(ZOOKEEPER_ALIAS)
			.withEnv("ZOOKEEPER_CLIENT_PORT", String.valueOf(ZOOKEEPER_PORT));

	@Container
	private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
			.dependsOn(zookeeperContainer)
			.withNetwork(NETWORK)
			.withExposedPorts(KAFKA_PORT_OUTSIDE)
			.withNetworkAliases(KAFKA_ALIAS)
			.withExternalZookeeper(ZOOKEEPER_URL_INTERNAL)
			.withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://" + KAFKA_URL_OUTSIDE + ",BROKER://" + KAFKA_URL_INTERNAL)
			.withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:" + KAFKA_PORT_OUTSIDE + ",BROKER://0.0.0.0:" + KAFKA_PORT_INTERNAL)
			.withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,BROKER:PLAINTEXT")
			.withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "BROKER")
			.withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

	@Container
	private static final LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
			.withExposedPorts(LOCALSTACK_PORT)
			.withServices(LocalStackContainer.Service.S3)
			.withCopyFileToContainer(
					MountableFile.forClasspathResource("init-scripts/localstack/localstack-ready.sh"),
					"/etc/localstack/init/ready.d/localstack-ready.sh");


	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);

		registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
		registry.add("kafka.bootstrap.servers", kafkaContainer::getBootstrapServers);

		registry.add("resource.s3.url", () -> "http://" + LOCALHOST + ":" + localStackContainer.getMappedPort(LOCALSTACK_PORT));
	}

	@BeforeEach
	public void beforeEach() {
		resourceRepository.deleteAll();
	}

	/*@Test*/
	void testSaveFile_Success() throws IOException {
		// given
		byte[] fileBytes = resourceFile.getContentAsByteArray();
		String fileIdentifier = HashUtils.generateNameFromBytes(fileBytes);

		// when
		ResourceDTO result = resourceService.saveFile(fileBytes);
		Long resourceId = result.getId();

		// then
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(resourceId);

		ResourceEntity savedEntity = resourceRepository.findById(result.getId()).orElse(null);
		Assertions.assertNotNull(savedEntity);
		Assertions.assertEquals(fileIdentifier, savedEntity.getFileIdentifier());

		byte[] savedFileData = s3Service.readFile(testResourceBucketName, fileIdentifier);
		Assertions.assertNotNull(savedFileData);

		this.verifyKafkaMessage(testResourceTopicName, resourceId, resourceId);
	}

	private void verifyKafkaMessage(String topicName, Long expectedKey, Long expectedValue) {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafkaContainer.getBootstrapServers(), "resource-consumer-test-group", "true");
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
