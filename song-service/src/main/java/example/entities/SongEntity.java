package example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@NotNull
	@Size(min = 1, max = 100)
	private String name;
	@NotNull
	@Size(min = 1, max = 100)
	private String artist;
	@NotNull
	@Size(min = 1, max = 100)
	private String album;
	@NotNull
	@Pattern(regexp = "^(\\d{2}:(?:[0-5]\\d)|Unknown)$", message = "Duration must be in the format mm:ss, with leading zeros")
	private String duration;
	@NotNull
	@Min(value = 1900, message = "Year must not be less than 1900")
	@Max(value = 2099, message = "Year must not be greater than 2099")
	private Integer year;
}
