package example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
@Builder
public class SongRequestDTO {
	@NotNull(message = "Song ID is required")
	@Min(value = 1, message = "Must be a positive integer")
	private Long id;
	@NotNull(message = "Song name is required")
	@Length(min = 1, max = 100)
	private String name;
	@NotNull(message = "Song artist is required")
	@Length(min = 1, max = 100)
	private String artist;
	@NotNull(message = "Song album is required")
	@Length(min = 1, max = 100)
	private String album;
	@NotNull(message = "Song duration is required")
	@Pattern(regexp = "^(\\d{2}:(?:[0-5]\\d)|Unknown)$", message = "Duration must be in mm:ss format with leading zeros")
	private String duration;
	@NotNull(message = "Song year is required")
	@Pattern(regexp = "^(19\\d{2}|20\\d{2})$", message = "Year must be between 1900 and 2099")
	private String year;
}
