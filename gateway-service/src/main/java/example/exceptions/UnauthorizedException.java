package example.exceptions;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UnauthorizedException extends ResponseStatusException {

	private final String message;

	public UnauthorizedException(HttpStatus status) {
		super(status);
		this.message = StringUtils.EMPTY;
	}

	public UnauthorizedException(HttpStatus status, String message) {
		super(status);
		this.message = message;
	}
}