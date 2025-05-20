package component.steps;


import example.dto.ResourceDTO;
import example.entities.ResourceEntity;
import example.exceptions.ResourceSaveException;
import example.messaging.kafka.producers.ResourceProducer;
import example.repositories.ResourceRepository;
import example.services.ConstraintsService;
import example.services.S3Service;
import example.services.impl.ResourceServiceImpl;
import example.utils.HashUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;

import java.io.IOException;


public class ResourceServiceSaveFileStepsTest {
	@Autowired
	private ResourceServiceImpl resourceService;
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private S3Service s3Service;

	@Value("classpath:files/test-file.mp3")
	private Resource resourceFile;

	private static final String RESOURCE_BUCKET_NAME = "resource-test-bucket";

	private byte[] fileBytes;
	private String fileIdentifier;
	private ResourceDTO result;
	private Exception exception;

	@Given("a valid file")
	public void aFileWithContent() throws IOException {
		fileBytes = resourceFile.getContentAsByteArray();
		fileIdentifier = HashUtils.generateNameFromBytes(fileBytes);
	}

	@When("the file is saved")
	public void theFileIsSaved() {
		try {
			result = resourceService.saveFile(fileBytes);
		} catch (Exception e) {
			exception = e;
		}
	}

	@When("the file is saved with null value")
	public void theFileIsSavedWithNullValue() {
		try {
			result = resourceService.saveFile(null);
		} catch (Exception e) {
			exception = e;
		}
	}

	@Then("the file should be uploaded to S3 with identifier")
	public void theFileShouldBeUploadedToS3() {
		byte[] fileContent = s3Service.readFile(RESOURCE_BUCKET_NAME, fileIdentifier);
		Assertions.assertNotNull(fileContent);
	}

	@Then("the file identifier should be saved in the database")
	public void theFileMetadataShouldBeSavedInTheDatabase() {
		ResourceEntity entity = resourceRepository.findById(result.getId())
				.orElseThrow(() -> new EntityNotFoundException("Entity with id " + result.getId() + " not found"));
		Assertions.assertNotNull(entity.getFileIdentifier());
		Assertions.assertEquals(entity.getFileIdentifier(), fileIdentifier);
	}

	@Then("the returned ResourceDTO should have id")
	public void theReturnedResourceDTOShouldHaveFileIdentifier() {
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.getId());
	}

	@Then("a ResourceSaveException should be thrown")
	public void aResourceSaveExceptionShouldBeThrown() {
		Assertions.assertNotNull(exception);
		Assertions.assertTrue(exception instanceof ResourceSaveException);
	}

}
