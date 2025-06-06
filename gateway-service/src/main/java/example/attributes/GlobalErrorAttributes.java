package example.attributes;

import example.constants.GatewayConstants;
import example.exceptions.UnauthorizedException;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;
import java.util.Optional;


@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

	@Resource
	private Tracer tracer;

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		Map<String, Object> errorResponse = super.getErrorAttributes(request, options);
		HttpStatus status = HttpStatus.valueOf((Integer) errorResponse.get(GatewayConstants.ERROR_OPTIONS_STATUS));

		errorResponse.put(GatewayConstants.ERROR_OPTIONS_MESSAGE, status.getReasonPhrase());
		errorResponse.put(GatewayConstants.ERROR_OPTIONS_TRACE_ID, Optional.of(tracer.currentSpan())
				.map(Span::context)
				.map(TraceContext::traceId)
				.orElse(GatewayConstants.TRACE_ID_NOT_FOUND_DEFAULT_VALUE));
		this.processError(super.getError(request), errorResponse);
		//customize error response

		return errorResponse;
	}

	protected void processError(Throwable error, Map<String, Object> errorResponse) {
		if (error instanceof UnauthorizedException e) {
			errorResponse.put(GatewayConstants.ERROR_OPTIONS_MESSAGE, e.getMessage());
			errorResponse.put(GatewayConstants.ERROR_OPTIONS_STATUS, e.getStatusCode().value());
		}
	}

}
