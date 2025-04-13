package example.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ResourceDTO {
	private Long id;
	@JsonIgnore
	private byte[] data;
}
