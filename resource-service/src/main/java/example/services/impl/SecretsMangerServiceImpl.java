package example.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.services.SecretsManagerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;


@Service
public class SecretsMangerServiceImpl implements SecretsManagerService {

	@Resource
	private SecretsManagerClient secretsManagerClient;

	@Resource
	private ObjectMapper objectMapper;

	public Map<String, String> getSecretAsMap(String secretName) {
		try {
			GetSecretValueRequest request = GetSecretValueRequest.builder()
					.secretId(secretName)
					.build();
			GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
			return objectMapper.readValue(response.secretString(), Map.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch secret from AWS Secrets Manager", e);
		}
	}

}
