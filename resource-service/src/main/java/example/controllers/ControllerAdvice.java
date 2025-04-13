package example.controllers;


import example.dto.ErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorDTO> handleEntityNotFound(EntityNotFoundException exception) {
		return this.generalTemplateFill(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorDTO> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorDTO> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST,
				"Field " + exception.getPropertyName() + " should be a valid type, used : " + exception.getValue());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorDTO> handleIllegalArgument(IllegalArgumentException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorDTO> handleConstraintViolation(ConstraintViolationException exception) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErrorDTO.builder()
						.errorCode(HttpStatus.BAD_REQUEST.toString())
						.errorMessage(this.getConstraintErrorMessage(exception))
						.details(this.getConstraintDetails(exception))
						.build());
	}

	private ResponseEntity<ErrorDTO> generalTemplateFill(HttpStatus status, String errorMessage) {
		return ResponseEntity
				.status(status)
				.body(ErrorDTO.builder()
						.errorCode(status.toString())
						.errorMessage(errorMessage)
						.build());
	}

	private String getConstraintErrorMessage(ConstraintViolationException exception) {
		return "Validation error: " + exception.getConstraintViolations().stream()
				.map(con -> {
					Object invalidValue = con.getInvalidValue();
					int length = invalidValue instanceof String ? ((String) invalidValue).length() : -1;
					return String.format("Field '%s' has invalid value '%s' (length: %d)",
							con.getPropertyPath().toString(),
							invalidValue,
							length);
				})
				.collect(Collectors.joining("; "));
	}

	private Map<String, String> getConstraintDetails(ConstraintViolationException exception) {
		AtomicInteger index = new AtomicInteger(1);
		return exception.getConstraintViolations()
				.stream()
				.collect(Collectors.toMap(
						con -> String.format("%d. %s", index.getAndIncrement(), con.getPropertyPath()),
						con -> {
							Object invalidValue = con.getInvalidValue();
							int length = invalidValue instanceof String ? ((String) invalidValue).length() : -1;
							return String.format("%s (length: %d)", con.getMessage(), length);
						}
				));
	}

}
