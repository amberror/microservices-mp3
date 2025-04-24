package example.exceptions;

import lombok.Getter;


@Getter
public class ResourceSaveException  extends RuntimeException {

	private final String errorMessage;

	public ResourceSaveException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

}
