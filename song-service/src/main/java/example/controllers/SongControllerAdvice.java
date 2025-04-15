package example.controllers;

import example.dto.ErrorDTO;
import example.exceptions.InvalidArgumentException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestControllerAdvice
public class SongControllerAdvice {

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

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<ErrorDTO> handleInvalidArgument(InvalidArgumentException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDTO> handleNotValidArgument(MethodArgumentNotValidException exception) {
		return this.generalTemplateWithDetailsFill(HttpStatus.BAD_REQUEST, "Validation error",
				exception.getBindingResult().getFieldErrors().stream()
						.collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage)));
	}

	private ResponseEntity<ErrorDTO> generalTemplateWithDetailsFill(HttpStatus status, String errorMessage, Map<String, String> details) {
		ResponseEntity<ErrorDTO> response = this.generalTemplateFill(status, errorMessage);
		response.getBody().setDetails(details);
		return response;
	}

	private ResponseEntity<ErrorDTO> generalTemplateFill(HttpStatus status, String errorMessage) {
		return ResponseEntity
				.status(status)
				.body(ErrorDTO.builder()
						.errorCode(String.valueOf(status.value()))
						.errorMessage(errorMessage)
						.build());
	}

	private Object getNullValueSafe(Object value) {
		return Optional.ofNullable(value).orElse("null");
	}
}
