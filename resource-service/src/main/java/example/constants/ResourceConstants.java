package example.constants;

public class ResourceConstants {
	public static final String AUDIO_MPEG = "audio/mpeg";
	public static final String COMMA_SEPARATOR = ",";
	public static final String SHA_256 = "SHA-256";

	//Exceptions
	public static final String INVALID_ID_FORMAT_MESSAGE_TEMPLATE = "Invalid ID format: '%s'. Only positive integers are allowed";
	public static final String INVALID_ID_VALUE_MESSAGE_TEMPLATE = "Invalid value '%s' for ID. Must be a positive integer";
	public static final String CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE = "CSV string is too long: received %d characters, maximum allowed is 200";
	public static final String RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE = "Resource with ID=%d not found";
	public static final String INVALID_FILE_FORMAT_MESSAGE_TEMPLATE = "Invalid file format: %s. Only MP3 files are allowed";
	public static final String ID_POSITIVE_VALUE_MESSAGE_TEMPLATE = "Invalid value '%s' for %s. Must be a positive integer";
	public static final String SAVE_S3_FAILED_MESSAGE_TEMPLATE = "Error occurred during resource save to s3 bucket %s";
	public static final String REST_DELETE_FILE_FAILED_MESSAGE = "Failed to delete resource's metadata in song service";
}
