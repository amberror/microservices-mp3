package example;

import example.constants.GatewayConstants;
import example.enums.PermissionType;
import example.exceptions.UnauthorizedException;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Component
@Slf4j
public class JwtAuthenticationGatewayFilterFactory extends
		AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

	@Resource
	private JwtDecoder jwtDecoder;

	public JwtAuthenticationGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) ->
				config.isAllowAnonymous() || this.processJWT(exchange.getRequest(), config) ?
						chain.filter(exchange) :
						this.returnUnauthorizedResponse(exchange.getResponse());
	}

	protected boolean processJWT(ServerHttpRequest request, Config config) {
		boolean allowed = false;
		try {
			allowed = this.accessAllowed(this.getJwt(request), config.getRoles().stream().map(PermissionType::valueOf).toList());
			if(!allowed) {
				throw new UnauthorizedException(HttpStatus.FORBIDDEN, GatewayConstants.PERMISSION_DENIED_MESSAGE);
			}
		} catch (JwtException e) {
			throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, GatewayConstants.INVALID_TOKEN_MESSAGE);
		}
		return allowed;
	}

	protected Jwt getJwt(ServerHttpRequest request) {
		return Optional.ofNullable(StringUtils.substringAfter(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION), GatewayConstants.BEARER_PREFIX))
				.map(token -> jwtDecoder.decode(token))
				.orElseThrow(() -> new UnauthorizedException(HttpStatus.UNAUTHORIZED, GatewayConstants.AUTHORIZATION_TOKEN_MISSED_MESSAGE));
	}

	protected boolean accessAllowed(Jwt token, List<PermissionType> permissionTypes) {
		return token.hasClaim(GatewayConstants.CLAIMS_ROLE) &&
				permissionTypes.stream()
						.anyMatch(permissionType -> permissionType.getLabel().equals(token.getClaim(GatewayConstants.CLAIMS_ROLE)) ||
							permissionType.isHigherRole(PermissionType.valueOf(token.getClaim(GatewayConstants.CLAIMS_ROLE))));
	}

	protected Mono<Void> returnUnauthorizedResponse(ServerHttpResponse response) {
		//Alternative fail fast approach without exception handling
		//response.setStatusCode(HttpStatus.UNAUTHORIZED);
		//return response.setComplete();
		throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, GatewayConstants.DEFAULT_UNAUTHORIZED_MESSAGE);
	}

	@Getter
	@Setter
	public static class Config {
		private boolean allowAnonymous;
		private List<String> roles;
	}
}
