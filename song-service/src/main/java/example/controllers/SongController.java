package example.controllers;

import example.dto.SongRequestDTO;
import example.dto.SongResponseDTO;
import example.services.SongService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/songs")
@Validated
public class SongController {

	@Resource
	private SongService songService;

	@GetMapping("/{id}")
	public ResponseEntity<SongResponseDTO> getSong(@PathVariable(name = "id") @Min(1) @Positive Long id) {
		return ResponseEntity.ok(songService.getSong(id));
	}

	@PostMapping
	public ResponseEntity<Map<String, Long>> saveSong(@RequestBody @Valid SongRequestDTO dto) {
		return ResponseEntity.ok(Map.of("id", songService.saveSong(dto)));
	}

	@DeleteMapping
	public ResponseEntity<Map<String, List<Long>>> deleteSongs(@RequestParam(name = "id", required = false) @Length(max = 200) String ids) {
		return ResponseEntity.ok(Map.of("ids", songService.deleteSongs(ids)));
	}

}
