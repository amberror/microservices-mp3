package e2e.config;

import example.ResourceServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;


@CucumberContextConfiguration
@SpringBootTest(
		classes = ResourceServiceApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-e2e.properties")
@Testcontainers
public class CucumberSpringConfiguration {

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
		postgresContainer.start();
		kafkaContainer.start();
		localStackContainer.start();

		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);

		registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
		registry.add("kafka.bootstrap.servers", kafkaContainer::getBootstrapServers);

		registry.add("resource.s3.url", () -> "http://" + LOCALHOST + ":" + localStackContainer.getMappedPort(LOCALSTACK_PORT));
	}
}

