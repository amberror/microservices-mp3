package example.controllers;


import example.constants.ResourceConstants;
import example.dto.ErrorDTO;
import example.exceptions.InvalidArgumentException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;


@RestControllerAdvice
public class ResourceControllerAdvice {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorDTO> handleEntityNotFound(EntityNotFoundException exception) {
		return this.generalTemplateFill(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorDTO> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST,
				String.format(ResourceConstants.INVALID_FILE_FORMAT_MESSAGE_TEMPLATE, exception.getContentType()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorDTO> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST,
				String.format(ResourceConstants.ID_POSITIVE_VALUE_MESSAGE_TEMPLATE, this.getNullValueSafe(exception.getValue()), exception.getPropertyName().toUpperCase()));
	}

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<ErrorDTO> handleInvalidArgument(InvalidArgumentException exception) {
		return this.generalTemplateFill(HttpStatus.BAD_REQUEST, exception.getMessage());
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
