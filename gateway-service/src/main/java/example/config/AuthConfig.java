package example.config;

import example.constants.GatewayConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@Configuration
public class AuthConfig {

	@Value("${auth.service.default.issuer}")
	private String issuer;

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(issuer + GatewayConstants.JWKS_PATH).build();
	}

}
