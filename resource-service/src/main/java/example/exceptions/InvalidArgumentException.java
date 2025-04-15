package example.exceptions;

import lombok.Getter;


@Getter
public class InvalidArgumentException extends RuntimeException {
	private final String errorMessage;

	public InvalidArgumentException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
}
