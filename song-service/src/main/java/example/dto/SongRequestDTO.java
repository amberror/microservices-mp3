package example.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
@Builder
public class SongRequestDTO {
	@NotNull
	@Positive
	@Min(1)
	private Long id;
	@NotNull
	@Length(min = 1, max = 100)
	private String name;
	@NotNull
	@Length(min = 1, max = 100)
	private String artist;
	@NotNull
	@Length(min = 1, max = 100)
	private String album;
	@NotNull
	@Pattern(regexp = "^(\\d{2}:(?:[0-5]\\d)|Unknown)$", message = "Duration must be in the format mm:ss, with leading zeros")
	private String duration;
	@NotNull
	@Pattern(regexp = "^(19\\d{2}|20\\d{2})$", message = "Year must be value from 1900 to 2099")
	private String year;
}
