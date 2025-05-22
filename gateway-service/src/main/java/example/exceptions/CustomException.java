package example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

//template for further authorization, rate limit or other custom exceptions
public class CustomException extends ResponseStatusException {

	public CustomException(HttpStatus status) {
		super(status);
	}
}