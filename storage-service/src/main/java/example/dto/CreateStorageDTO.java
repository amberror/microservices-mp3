package example.dto;

import example.annotations.ValidEnum;
import example.enums.StorageType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CreateStorageDTO {

	@NotNull(message = "storageType can not be empty")
	@ValidEnum(enumClass = StorageType.class)
	private String storageType;

	@NotNull(message = "bucket can not be empty")
	private String bucket;

	@NotNull(message = "path can not be empty")
	private String path;
}
