package constracts.tests;

import constracts.config.ContractTestConfig;
import example.ResourceProcessorApplication;
import example.dto.SongRequestDTO;
import example.services.SongIntegrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;


@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = {ContractTestConfig.class, ResourceProcessorApplication.class}
)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureStubRunner(
		ids = "example:song-service",
		stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class SongServiceRestContractsTest {

	@Autowired
	private SongIntegrationService songIntegrationService;

	@Test
	public void should_save_resource_metadata() {
		var dto = SongRequestDTO.builder()
				.id(1L)
				.name("Bohemian Rhapsody")
				.artist("Queen")
				.album("A Night at the Opera")
				.duration("05:55")
				.year(1975)
				.build();

		var result = songIntegrationService.saveMetadata(dto);

		Assertions.assertTrue(result);
	}
}
