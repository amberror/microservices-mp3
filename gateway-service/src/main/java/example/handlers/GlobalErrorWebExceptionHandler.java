package example.handlers;

import example.attributes.GlobalErrorAttributes;
import example.constants.GatewayConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@Component
@Order(PriorityOrdered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);


	public GlobalErrorWebExceptionHandler(GlobalErrorAttributes attributes,
										  ApplicationContext applicationContext,
										  ServerCodecConfigurer serverCodecConfigurer) {
		super(attributes, new WebProperties.Resources(), applicationContext);
		super.setMessageWriters(serverCodecConfigurer.getWriters());
		super.setMessageReaders(serverCodecConfigurer.getReaders());
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
		HttpStatus status = HttpStatus.valueOf((int) errorPropertiesMap.get(GatewayConstants.ERROR_OPTIONS_STATUS));
		LOG.info("[EXCEPTION-HANDLER] Handled status : [{}], message : [{}], path : [{}], traceId: [{}]",
				status,
				errorPropertiesMap.get(GatewayConstants.ERROR_OPTIONS_MESSAGE),
				errorPropertiesMap.get(GatewayConstants.ERROR_OPTIONS_PATH),
				errorPropertiesMap.get(GatewayConstants.ERROR_OPTIONS_TRACE_ID));
		return ServerResponse.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(errorPropertiesMap));
	}
}