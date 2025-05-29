package example.constants;

public class StorageServiceConstants {
	public static final String COMMA_SEPARATOR = ",";

	//Exceptions
	public static final String CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE = "CSV string is too long: received %d characters, maximum allowed is 200";
	public static final String INVALID_ID_VALUE_MESSAGE_TEMPLATE = "Invalid value '%s' for ID. Must be a positive integer";
	public static final String INVALID_ID_FORMAT_MESSAGE_TEMPLATE = "Invalid ID format: '%s'. Only positive integers are allowed";
	public static final String ENTITY_ALREADY_EXISTS_MESSAGE_TEMPLATE = "Storage in bucket [%s] and path [%s] already exists";
	public static final String ENUM_VALIDATION_PROCESSOR_INVALID_VALUE_MESSAGE_TEMPLATE = "Invalid value: %s. Allowed values are: %s";

}
