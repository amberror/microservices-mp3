package example.messaging.kafka.services.impl;

import example.dto.SongRequestDTO;
import example.exceptions.ResourceException;
import example.messaging.kafka.services.MessagingService;
import example.models.FileMetadataModel;
import example.services.MediaMetadataService;
import example.services.ResourceIntegrationService;
import example.services.SongIntegrationService;
import jakarta.annotation.Resource;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@Service
public class MessagingServiceImpl implements MessagingService {

	@Resource
	private ResourceIntegrationService resourceIntegrationService;

	@Resource
	private SongIntegrationService songIntegrationService;

	@Resource
	private MediaMetadataService mediaMetadataService;

	@Resource
	private ConversionService conversionService;

	@Override
	public void processResourceEvent(Long resourceId) {
		FileMetadataModel metadata = mediaMetadataService.getFileMetadata(resourceIntegrationService.getResource(resourceId))
				.orElseThrow(() -> new ResourceException("Can not extract metadata from file with id " + resourceId));
		SongRequestDTO requestDTO = conversionService.convert(metadata, SongRequestDTO.class);
		requestDTO.setId(resourceId);
		if (!songIntegrationService.saveMetadata(requestDTO)) {
			throw new RestClientException("Failed to save resource's metadata in song service");
		}
	}
}
