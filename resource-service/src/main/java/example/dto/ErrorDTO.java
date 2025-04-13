package example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;


@Data
@Builder
public class ErrorDTO {
	private String errorMessage;
	private String errorCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> details;
}
