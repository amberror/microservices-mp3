package example.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import example.constants.AuthConstants;
import example.enums.PermissionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration(proxyBeanMethods = false)
public class AuthConfig {

	@Value("${auth.service.default.user.secret}")
	private String userSecret;

	@Value("${auth.service.default.admin.secret}")
	private String adminSecret;

	@Value("${auth.service.default.issuer}")
	private String issuer;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(withDefaults());
		return http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(withDefaults())).build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		//TODO: do not store credentials in memory for production
		return new InMemoryRegisteredClientRepository(
				createUser(PermissionType.USER, userSecret),
				createUser(PermissionType.ADMIN, adminSecret));
	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
		return context -> {
			if (context.getTokenType().getValue().equals(AuthConstants.ACCESS_TOKEN_NAME)) {
				switch (PermissionType.valueOf(context.getRegisteredClient().getClientId())) {
					case ADMIN -> context.getClaims().claim(AuthConstants.CLAIMS_ROLE, PermissionType.ADMIN);
					case USER -> context.getClaims().claim(AuthConstants.CLAIMS_ROLE, PermissionType.USER);
					default -> throw new IllegalArgumentException(AuthConstants.UNEXPECTED_USER_EXCEPTION_MESSAGE);
				}
			}
		};
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = generateRsa();
		return (jwkSelector, securityContext) -> jwkSelector.select(new JWKSet(rsaKey));
	}

	private static RSAKey generateRsa() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		return new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
	}

	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(AuthConstants.RSA_ALG);
			keyPairGenerator.initialize(AuthConstants.DEFAULT_RSA_KEY_SIZE);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	private RegisteredClient createUser(PermissionType permissionType, String secret) {
		return RegisteredClient
				.withId(UUID.randomUUID().toString())
				.clientId(permissionType.getLabel())
				.clientSecret(this.bCryptPasswordEncoder().encode(secret))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.tokenSettings(TokenSettings.builder()
						.accessTokenTimeToLive(Duration.ofMinutes(AuthConstants.DEFAULT_JWT_TTL))
						.build())
				.scope(permissionType.getLabel())
				.build();
	}

	@Bean
	public AuthorizationServerSettings providerSettings() {
		return AuthorizationServerSettings.builder()
				.issuer(issuer)
				.build();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}