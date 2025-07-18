package example.constants;

public class ResourceConstants {
	public static final String AUDIO_MPEG = "audio/mpeg";
	public static final String COMMA_SEPARATOR = ",";
	public static final String SHA_256 = "SHA-256";
	public static final String DATASOURCE_URL_SECRET_NAME = "spring.datasource.url";
	public static final String DATASOURCE_USERNAME_SECRET_NAME = "spring.datasource.username";
	public static final String DATASOURCE_PASSWORD_SECRET_NAME = "spring.datasource.password";
	public static final String DATASOURCE_DRIVER_SECRET_NAME = "spring.datasource.driver-class-name";


	//Exceptions
	public static final String INVALID_ID_FORMAT_MESSAGE_TEMPLATE = "Invalid ID format: '%s'. Only positive integers are allowed";
	public static final String INVALID_ID_VALUE_MESSAGE_TEMPLATE = "Invalid value '%s' for ID. Must be a positive integer";
	public static final String CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE = "CSV string is too long: received %d characters, maximum allowed is 200";
	public static final String RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE = "Resource with ID=%d not found";
	public static final String INVALID_FILE_FORMAT_MESSAGE_TEMPLATE = "Invalid file format: %s. Only MP3 files are allowed";
	public static final String ID_POSITIVE_VALUE_MESSAGE_TEMPLATE = "Invalid value '%s' for %s. Must be a positive integer";
	public static final String SAVE_S3_FAILED_MESSAGE_TEMPLATE = "Error occurred during resource save to s3 bucket %s";
	public static final String REST_DELETE_FILE_FAILED_MESSAGE = "Failed to delete resource's metadata in song service";
	public static final String STAGING_STORAGE_DELETION_FAILED_MESSAGE_TEMPLATE = "Error during file deletion from staging storage. Staging data [%s], file key [%s]";
	public static final String INVALID_MESSAGE_PRODUCER_SERVICE_TYPE = "Invalid resource producer service type: %s";
	public static final String INVALID_MESSAGE_CONSUMER_SERVICE_TYPE = "Invalid resource result consumer service type: %s";
}
