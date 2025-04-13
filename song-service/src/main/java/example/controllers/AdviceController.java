package example.controllers;

import example.dto.ErrorDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RestControllerAdvice
public class AdviceController {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorDTO> handleEntityNotFound(EntityNotFoundException exception) {
		return this.generalTemplateFill(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<ErrorDTO> handleEntityConflict(EntityExistsException exception) {
		return this.generalTemplateFill(HttpStatus.CONFLICT, exception.getMessage());
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
						.errorMessage(this.getConstraintErrorMessage(exception.getConstraintViolations()
								.stream()
								.collect(Collectors.toMap(
										con -> con.getPropertyPath().toString(),
										con -> this.getNullValueSafe(con.getInvalidValue()))), Boolean.TRUE))
						.details(this.getConstraintDetails(exception.getConstraintViolations()
								.stream()
								.collect(Collectors.toMap(
										con -> new AbstractMap.SimpleImmutableEntry<>(con.getPropertyPath().toString(), con.getMessage()),
										con -> this.getNullValueSafe(con.getInvalidValue()))), Boolean.TRUE))
						.build());
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDTO> handleNotValidArgument(MethodArgumentNotValidException exception) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErrorDTO.builder()
						.errorCode(HttpStatus.BAD_REQUEST.toString())
						.errorMessage(this.getConstraintErrorMessage(exception.getBindingResult().getFieldErrors()
								.stream()
								.collect(Collectors.toMap(FieldError::getField, err -> this.getNullValueSafe(err.getRejectedValue()))), Boolean.FALSE))
						.details(this.getConstraintDetails(exception.getBindingResult().getFieldErrors()
								.stream()
								.collect(Collectors.toMap(
										err ->  new AbstractMap.SimpleImmutableEntry<>(err.getField(), err.getDefaultMessage()),
										err -> this.getNullValueSafe(err.getRejectedValue()))), Boolean.FALSE))
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

	private String getConstraintErrorMessage(Map<String, Object> fieldErrorMap, boolean lengthInfo) {
		return "Validation error: " + fieldErrorMap.entrySet().stream()
				.map(mapUnit -> this.formatConstraintErrorMessage("Field '%s' has invalid value '%s' ", lengthInfo, mapUnit.getKey(), mapUnit.getValue()))
				.collect(Collectors.joining("; "));
	}

	private int getValueLength(Object value) {
		return value instanceof String ? ((String) value).length() : -1;
	}

	private Map<String, String> getConstraintDetails(Map<Map.Entry<String, String>, Object> fieldErrorMap, boolean lengthInfo) {
		AtomicInteger index = new AtomicInteger(1);
		return fieldErrorMap.entrySet()
				.stream()
				.collect(Collectors.toMap(
						mapUnit -> String.format("%d. %s", index.getAndIncrement(), mapUnit.getKey().getKey()),
						mapUnit -> this.formatConstraintErrorMessage("%s , used value %s ", lengthInfo, mapUnit.getKey().getValue(), mapUnit.getValue())));
	}

	private String formatConstraintErrorMessage(String template, boolean lengthInfo, String param, Object invalidValue) {
		Object value = this.getNullValueSafe(invalidValue);
		return lengthInfo ?
				String.format(template + "(length: %d)", param, value, this.getValueLength(value)) :
				String.format(template, param, value);
	}

	private Object getNullValueSafe(Object value) {
		return Optional.ofNullable(value).orElse("null");
	}
}
