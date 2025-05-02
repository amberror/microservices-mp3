package example.exceptions;

import lombok.Getter;


@Getter
public class ResourceException extends RuntimeException {
	private final String message;

	public ResourceException(String message) {
		super(message);
		this.message = message;
	}

}
