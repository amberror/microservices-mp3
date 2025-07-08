package example.cloud.secretmanager;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;


@Configuration
public class SecretManagerConfig {

	@Value("${cloud.aws.secrets.manager.resource.region}")
	private String secretRegion;

	@Value("${cloud.aws.secrets.manager.resource.accessKeyId}")
	private String secretAccessKey;

	@Value("${cloud.aws.secrets.manager.resource.secretAccessKey}")
	private String secretSecret;

	@Bean
	public SecretsManagerClient secretsManagerClient() {
		return SecretsManagerClient.builder()
				.region(Region.of(secretRegion))
				.credentialsProvider(
						StaticCredentialsProvider.create(
								AwsBasicCredentials.create(secretAccessKey, secretSecret)
						)
				)
				.build();
	}
}
