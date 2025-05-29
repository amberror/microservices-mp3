package example.annotations.processors;

import example.annotations.ValidEnum;
import example.constants.StorageServiceConstants;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;


public class EnumValidationProcessor implements ConstraintValidator<ValidEnum, String> {

	private Class<? extends Enum<?>> enumClass;
	private String defaultMessage;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		this.enumClass = constraintAnnotation.enumClass();
		this.defaultMessage = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		boolean isValid = Arrays.stream(enumClass.getEnumConstants())
				.map(Enum::name)
				.anyMatch(enumValue -> enumValue.equals(value));

		if (!isValid && StringUtils.isBlank(this.defaultMessage)) {
			String validValues = Arrays.toString(enumClass.getEnumConstants());
			addConstraintViolation(context,
					String.format(StorageServiceConstants.ENUM_VALIDATION_PROCESSOR_INVALID_VALUE_MESSAGE_TEMPLATE,
							value, validValues));
		}

		return isValid;
	}

	private void addConstraintViolation(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}