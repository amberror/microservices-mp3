package example.services;

import java.util.Map;


public interface SecretsManagerService {
	Map<String, String> getSecretAsMap(String secretName);
}
