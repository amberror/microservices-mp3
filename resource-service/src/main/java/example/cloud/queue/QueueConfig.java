package example.cloud.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;


@Configuration
public class QueueConfig {

	@Value("${cloud.aws.sqs.region}")
	private String SQSRegion;

	@Value("${cloud.aws.sqs.access.key}")
	private String SQSAccessKey;

	@Value("${cloud.aws.sqs.secret}")
	private String SQSSecretKey;

	@Bean
	@Lazy
	public SqsAsyncClient queueMessagingTemplate() {
		return SqsAsyncClient.builder()
				.region(Region.of(SQSRegion))
				.credentialsProvider(
						StaticCredentialsProvider.create(
								AwsBasicCredentials.create(SQSAccessKey, SQSSecretKey)
						)
				)
				.build();
	}
}
