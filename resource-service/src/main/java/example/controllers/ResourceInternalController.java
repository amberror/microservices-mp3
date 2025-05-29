package example.controllers;

import example.constants.ResourceConstants;
import example.enums.StorageType;
import example.services.ResourceService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("internal/resources")
@Validated
public class ResourceInternalController {

	private static final String ID = "id";

	@Resource
	private ResourceService resourceService;

	@GetMapping("/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable(name = ID) Long id, @RequestParam String storageType) {
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(ResourceConstants.AUDIO_MPEG))
				.body(resourceService.getFileDataFromStorage(id, StorageType.valueOf(storageType)));
	}
}
