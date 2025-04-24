package example.cloud.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;


@Configuration
public class S3Config {

	@Value("${resource.s3.url}")
	private String s3Url;

	@Value("${resource.s3.region}")
	private String s3Region;

	@Value("${resource.s3.accessKeyId}")
	private String s3AccessKeyId;

	@Value("${resource.s3.secretAccessKey}")
	private String s3SecretAccessKey;

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
				.endpointOverride(URI.create(s3Url))
				.region(Region.of(s3Region))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(s3AccessKeyId, s3SecretAccessKey)
				))
				.serviceConfiguration(S3Configuration.builder()
						.pathStyleAccessEnabled(Boolean.TRUE)
						.build())
				.build();
	}

}
