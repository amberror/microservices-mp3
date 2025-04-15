package example.controllers;

import example.dto.SongRequestDTO;
import example.dto.SongResponseDTO;
import example.services.SongService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/songs")
@Validated
public class SongController {
	private static final String ID = "id";
	private static final String IDS = "ids";

	@Resource
	private SongService songService;

	@GetMapping("/{id}")
	public ResponseEntity<SongResponseDTO> getSong(@PathVariable(name = ID) Long id) {
		return ResponseEntity.ok(songService.getSong(id));
	}

	@PostMapping
	public ResponseEntity<Map<String, Long>> saveSong(@RequestBody @Valid SongRequestDTO dto) {
		return ResponseEntity.ok(Map.of(ID, songService.saveSong(dto)));
	}

	@DeleteMapping
	public ResponseEntity<Map<String, List<Long>>> deleteSongs(@RequestParam(name = ID, required = false) String ids) {
		return ResponseEntity.ok(Map.of(IDS, songService.deleteSongs(ids)));
	}

}
