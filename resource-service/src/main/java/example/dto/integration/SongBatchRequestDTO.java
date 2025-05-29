package example.dto.integration;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class SongBatchRequestDTO {
	private List<Long> ids;
}
