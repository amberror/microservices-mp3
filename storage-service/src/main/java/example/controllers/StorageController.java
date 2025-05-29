package example.controllers;

import example.annotations.ValidCsvIds;
import example.dto.CreateStorageDTO;
import example.dto.StorageResponseDTO;
import example.services.StorageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


@RestController
@RequestMapping("/storages")
@Validated
public class StorageController {
	private static final String ID = "id";
	private static final String IDS = "ids";

	@Resource
	private StorageService storageService;


	@GetMapping
	public ResponseEntity<List<StorageResponseDTO>> getStorages() {
		return ResponseEntity.ok(storageService.getStorages());
	}

	@PostMapping(consumes = APPLICATION_JSON)
	public ResponseEntity<Map<String, Long>> addStorage(@Valid @RequestBody CreateStorageDTO dto) {
		return ResponseEntity.ok(Map.of(ID, storageService.saveStorage(dto)));
	}

	@DeleteMapping
	public ResponseEntity<Map<String, List<Long>>> deleteStorages(
			@ValidCsvIds @RequestParam(name = ID, required = false) String ids) {
		return ResponseEntity.ok(Map.of(IDS, storageService.deleteStorages(ids)));
	}

}
