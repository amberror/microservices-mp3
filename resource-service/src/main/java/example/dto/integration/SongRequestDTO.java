package example.dto.integration;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SongRequestDTO {
	private Long id;
	private String name;
	private String artist;
	private String album;
	private String duration;
	private Integer year;
}