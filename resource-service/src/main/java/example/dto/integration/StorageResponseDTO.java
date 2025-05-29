package example.dto.integration;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@Builder
@ToString
public class StorageResponseDTO {
	private Long id;
	private String storageType;
	private String bucket;
	private String path;
}

