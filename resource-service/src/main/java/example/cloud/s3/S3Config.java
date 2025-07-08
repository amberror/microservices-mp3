package example.cloud.s3;

import example.enums.StorageServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;


@Configuration
public class S3Config {

	@Value("${cloud.aws.s3.resource.url}")
	private String s3Url;

	@Value("${cloud.aws.s3.resource.region}")
	private String s3Region;

	@Value("${cloud.aws.s3.resource.accessKeyId}")
	private String s3AccessKeyId;

	@Value("${cloud.aws.s3.resource.secretAccessKey}")
	private String s3SecretAccessKey;

	@Value("${storage.service.type}")
	private String storageServiceType;

	@Bean
	@Lazy
	public S3Client s3Client() {
		return switch (StorageServiceType.getByLabel(storageServiceType.toLowerCase())) {
			case LOCALSTACK -> this.getLocalstackS3Client();
			case AWS -> this.getAWSS3Client();
			default -> throw new IllegalArgumentException("Invalid storage service type: " + storageServiceType);
		};
	}

	private S3Client getLocalstackS3Client() {
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

	private S3Client getAWSS3Client() {
		return S3Client.builder()
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
