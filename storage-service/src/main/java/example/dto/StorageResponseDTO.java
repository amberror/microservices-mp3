package example.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class StorageResponseDTO {
	private Long id;
	private String storageType;
	private String bucket;
	private String path;
}
