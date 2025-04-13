package example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ResourceBatchDTO {
	private List<Long> ids;
}
