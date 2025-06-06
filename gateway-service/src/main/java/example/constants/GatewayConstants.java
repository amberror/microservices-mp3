package example.constants;

public class GatewayConstants {
	public static final String BEARER_PREFIX = "Bearer ";
	public static final String JWKS_PATH = "/oauth2/jwks";
	public static final String CLAIMS_ROLE = "role";
	public static final String DEFAULT_UNAUTHORIZED_MESSAGE = "Unauthorized request";
	public static final String AUTHORIZATION_TOKEN_MISSED_MESSAGE = "Authorization token missed";
	public static final String PERMISSION_DENIED_MESSAGE = "Restricted request, permission denied";
	public static final String INVALID_TOKEN_MESSAGE = "Token invalid, malformed or signature mismatch";
	public static final String ERROR_OPTIONS_MESSAGE = "message";
	public static final String ERROR_OPTIONS_TRACE_ID = "traceId";
	public static final String ERROR_OPTIONS_STATUS = "status";
	public static final String ERROR_OPTIONS_PATH = "path";
	public static final String TRACE_ID_NOT_FOUND_DEFAULT_VALUE = "No Trace ID";

}
