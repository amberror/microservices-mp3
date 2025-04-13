package example.controllers;

import example.constants.ResourceConstants;
import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.services.ResourceService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/resources")
@Validated
public class ResourceController {

	@Resource
	private ResourceService resourceService;

	@PostMapping(consumes = ResourceConstants.AUDIO_MPEG)
	public ResponseEntity<ResourceDTO> postFile(@RequestBody byte[] fileBytes) {
		return ResponseEntity.ok(resourceService.saveFile(fileBytes));
	}

	@GetMapping("/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable(name = "id") @Min(1) @Positive Long id) {
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(ResourceConstants.AUDIO_MPEG))
				.body(resourceService.getFile(id).getData());
	}

	@GetMapping("/exist/{id}")
	public ResponseEntity<Void> checkExist(@PathVariable(name = "id") @Min(1) @Positive Long id) {
		resourceService.checkExist(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<ResourceBatchDTO> deleteFile(@RequestParam(name = "id", required = false) @Length(max = 200) String ids) {
		return ResponseEntity.ok(resourceService.deleteFiles(ids));
	}
}
