package example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongEntity {
	@Id
	private Long id;
	@NotNull(message = "Song name is required")
	@Size(min = 1, max = 100)
	private String name;
	@NotNull(message = "Song artist is required")
	@Size(min = 1, max = 100)
	private String artist;
	@NotNull(message = "Song album is required")
	@Size(min = 1, max = 100)
	private String album;
	@NotNull(message = "Song duration is required")
	@Pattern(regexp = "^(\\d{2}:(?:[0-5]\\d)|Unknown)$", message = "Duration must be in mm:ss format with leading zeros")
	private String duration;
	@NotNull(message = "Song year is required")
	@Min(value = 1900, message = "Year must not be less than 1900")
	@Max(value = 2099, message = "Year must not be greater than 2099")
	private Integer year;
}
