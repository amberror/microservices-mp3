package example.annotations;

import example.annotations.processors.ValidCsvIdsProcessor;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCsvIdsProcessor.class)
public @interface ValidCsvIds {
	String message() default "Invalid ids.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
