package example.annotations.processors;

import example.annotations.ValidCsvIds;
import example.constants.StorageServiceConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

import static io.micrometer.common.util.StringUtils.isBlank;


public class ValidCsvIdsProcessor implements ConstraintValidator<ValidCsvIds, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		Long errorCount = 0L;

		if(isBlank(value)) {
			return true;
		}

		if(!this.isSizeValid(value)) {
			errorCount = this.addConstraintViolation(context,
					String.format(StorageServiceConstants.CSV_STRING_LENGTH_LIMITATION_MESSAGE_TEMPLATE, value.length()), errorCount);
		}

		try {
			if(this.negativeExist(value)) {
				errorCount = this.addConstraintViolation(context,
						String.format(StorageServiceConstants.INVALID_ID_VALUE_MESSAGE_TEMPLATE, value), errorCount);
			}
		} catch (IllegalArgumentException e) {
			errorCount = this.addConstraintViolation(context, e.getMessage(), errorCount);
		}

		return errorCount == 0L;
	}

	private boolean isSizeValid(String value) {
		return value.length() <= 200;
	}

	private boolean negativeExist(String value) {
		return this.parseStringCommaSeparated(value).stream().anyMatch(number -> !this.isPositive(number));
	}

	private boolean isPositive(Long value) {
		return value > 0L;
	}

	public List<Long> parseStringCommaSeparated(String value) {
		return Arrays.stream(value.split(StorageServiceConstants.COMMA_SEPARATOR))
				.map(id -> this.mapStringToLong(id, String.format(StorageServiceConstants.INVALID_ID_FORMAT_MESSAGE_TEMPLATE, id)))
				.toList();
	}

	private Long mapStringToLong(String value, String errorMessage) {
		Long result;
		try {
			result = Long.parseLong(value);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(errorMessage);
		}
		return result;
	}

	private Long addConstraintViolation(ConstraintValidatorContext context, String message, Long errorCount) {
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		return ++errorCount;
	}

}
