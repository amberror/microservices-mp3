package example.constants;

public class SongConstants {
	public static final String COMMA_SEPARATOR = ",";

	//Exceptions
	public static final String INVALID_ID_FORMAT_MESSAGE_TEMPLATE = "Invalid ID format: '%s'. Only positive integers are allowed";
	public static final String ENTITY_ALREADY_EXISTS_MESSAGE_TEMPLATE = "Metadata for resource ID=%d already exists";
	public static final String ENTITY_NOT_FOUND_MESSAGE_TEMPLATE = "Song metadata for ID=%d not found";
	public static final String INVALID_ID_VALUE_MESSAGE_TEMPLATE = "Invalid value '%s' for ID. Must be a positive integer";
	public static final String CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE = "CSV string is too long: received %d characters, maximum allowed is 200";
}
