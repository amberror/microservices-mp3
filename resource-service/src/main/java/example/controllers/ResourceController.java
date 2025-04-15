package example.controllers;

import example.constants.ResourceConstants;
import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.services.ResourceService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/resources")
@Validated
public class ResourceController {
	private static final String ID = "id";

	@Resource
	private ResourceService resourceService;

	@PostMapping(consumes = ResourceConstants.AUDIO_MPEG)
	public ResponseEntity<ResourceDTO> postFile(@RequestBody byte[] fileBytes) {
		return ResponseEntity.ok(resourceService.saveFile(fileBytes));
	}

	@GetMapping("/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable(name = ID) Long id) {
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(ResourceConstants.AUDIO_MPEG))
				.body(resourceService.getFile(id).getData());
	}

	@GetMapping("/exist/{id}")
	public ResponseEntity<Void> checkExist(@PathVariable(name = ID) Long id) {
		resourceService.checkExist(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<ResourceBatchDTO> deleteFile(@RequestParam(name = ID, required = false) String ids) {
		return ResponseEntity.ok(resourceService.deleteFiles(ids));
	}
}
