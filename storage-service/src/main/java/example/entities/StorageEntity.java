package example.entities;

import example.annotations.ValidEnum;
import example.enums.StorageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageEntity {
	@Id
	private Long id;
	@NotNull(message = "storageType can not be empty")
	@Enumerated(value = EnumType.STRING)
	private StorageType storageType;
	@NotNull(message = "bucket can not be empty")
	private String bucket;
	@NotNull(message = "path can not be empty")
	private String path;
}
