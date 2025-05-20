package e2e.steps;

import e2e.config.E2ETestsConfig;
import example.ResourceServiceApplication;
import example.constants.ResourceConstants;
import example.controllers.ResourceController;
import example.dto.ResourceDTO;
import example.services.ResourceService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@SpringBootTest(
		classes = {ResourceServiceApplication.class, E2ETestsConfig.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ResourceServiceE2EStepsTest {

	@Autowired
	private ResourceController resourceController;

	@Autowired
	private ResourceService resourceService;

	private final RestTemplate resourceServiceSelfRestTemplate = new RestTemplate();

	@Value("classpath:files/test-file.mp3")
	private Resource resourceFile;

	@LocalServerPort
	private int LOCAL_PORT;

	private static final String LOCALHOST = "http://localhost:";

	private static final String POST_RESOURCE_URI = "/resources";
	private static final String GET_RESOURCE_URI = "/resources/{id}";

	private byte[] testFileBytes;
	private Long testFileId;

	private ResponseEntity<ResourceDTO> responseResourceDto;

	private ResponseEntity<byte[]> responseByteArray;

	@Given("a valid audio file")
	public void aValidAudioFile(){
		try {
			testFileBytes = resourceFile.getContentAsByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Given("a valid file exists")
	public void aFileExistsWithID() {
		ResourceDTO resourceDTO;
		try {
			resourceDTO = resourceService.saveFile(resourceFile.getContentAsByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		testFileId = resourceDTO.getId();
	}

	@When("the file is uploaded")
	public void theFileIsUploaded() throws Exception {
		MultiValueMap<String, String> headers = MultiValueMap.fromSingleValue(
				Map.of(HttpHeaders.CONTENT_TYPE, ResourceConstants.AUDIO_MPEG));

		this.responseResourceDto = resourceServiceSelfRestTemplate.postForEntity(
				this.composeUrl(POST_RESOURCE_URI),
				new HttpEntity<>(testFileBytes, new HttpHeaders(headers)),
				ResourceDTO.class);
	}

	@When("the file is retrieved by ID")
	public void theFileIsRetrievedByID() {
		this.responseByteArray = resourceServiceSelfRestTemplate.getForEntity(
				this.composeUrl(GET_RESOURCE_URI),
				byte[].class,
				testFileId);
	}

	@Then("the response should contain the file's id")
	public void theResponseShouldContainTheFileId() {
		Assertions.assertNotNull(this.responseResourceDto.getBody());
		Assertions.assertNotNull(this.responseResourceDto.getBody().getId());
	}

	@Then("the response should contain the file's data")
	public void theResponseShouldContainTheFileData() {
		Assertions.assertNotNull(this.responseByteArray.getBody());
	}

	@Then("the response should contain application-json content-type header")
	public void theResponseShouldContainAudioMpegContentTypeHeader() {
		Assertions.assertNotNull(this.responseResourceDto.getHeaders());
		Assertions.assertTrue(this.responseResourceDto.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
		Assertions.assertEquals(this.responseResourceDto.getHeaders().asSingleValueMap().get(HttpHeaders.CONTENT_TYPE), ContentType.APPLICATION_JSON.getMimeType());
	}

	@Then("the response should contain {int} status")
	public void theResponseShouldContainOKStatus(int statusCode) {
		Assertions.assertTrue(this.responseResourceDto.getStatusCode().is2xxSuccessful());
		Assertions.assertEquals(this.responseResourceDto.getStatusCode().value(), statusCode);
	}

	@Then("the response should contain content-type audio-mpeg")
	public void theResponseShouldContainContentTypeAudioMpeg() {
		Assertions.assertNotNull(this.responseByteArray.getHeaders());
		Assertions.assertTrue(this.responseByteArray.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
		Assertions.assertEquals(this.responseByteArray.getHeaders().asSingleValueMap().get(HttpHeaders.CONTENT_TYPE), ResourceConstants.AUDIO_MPEG);
	}

	private String composeUrl(String uri) {
		return LOCALHOST + LOCAL_PORT + uri;
	}
}
