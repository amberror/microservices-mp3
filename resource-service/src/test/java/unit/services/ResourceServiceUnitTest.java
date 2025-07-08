package unit.services;

import example.constants.ResourceConstants;
import example.dto.ResourceBatchDTO;
import example.dto.ResourceDTO;
import example.dto.integration.SongBatchRequestDTO;
import example.dto.integration.StorageResponseDTO;
import example.entities.ResourceEntity;
import example.enums.StorageType;
import example.exceptions.InvalidArgumentException;
import example.messaging.common.producers.ResourceProducer;
import example.repositories.ResourceRepository;
import example.services.ConstraintsService;
import example.services.S3Service;
import example.services.SongIntegrationService;
import example.services.StorageIntegrationService;
import example.services.impl.ResourceServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
class ResourceServiceUnitTest {

	@InjectMocks
	private ResourceServiceImpl resourceService;

	@Mock
	private ResourceRepository resourceRepository;

	@Mock
	private ConversionService conversionService;

	@Mock
	private ConstraintsService constraintsService;

	@Mock
	private S3Service s3Service;

	@Mock
	private ResourceProducer resourceProducer;

	@Mock
	private SongIntegrationService songIntegrationService;

	@Mock
	private StorageIntegrationService storageIntegrationService;

	private static final Long MOCK_ID = 1L;
	private static final String FILE_IDENTIFIER = "hash484lfe94873495mfe";
	private static final byte[] FILE_CONTENT = "test content".getBytes();
	private static final String MOCK_BUCKET_NAME = "mock-bucket-name";
	private static final String MOCK_INVALID_ID_STRING = "abc,def";
	private static final List<Long> MOCK_ID_LIST = new ArrayList<>() {{
		add(1L);
		add(2L);
		add(3L);
	}};
	private static final String MOCK_ID_LIST_STRING = MOCK_ID_LIST.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(","));
	private ResourceEntity mockResourceEntity;
	private ResourceDTO mockResourceDTO;
	private StorageResponseDTO mockStageStorageData;
	private StorageResponseDTO mockPermanentStorageData;

	@BeforeEach
	public void beforeEach() {
		mockResourceEntity = this.createResourceEntity(MOCK_ID);
		mockResourceDTO = this.createResourceDTO(MOCK_ID);
		mockStageStorageData = StorageResponseDTO.builder()
				.storageType(StorageType.STAGING.toString())
				.path("/staging")
				.bucket("stage-bucket")
				.id(2L)
				.build();
		mockPermanentStorageData = StorageResponseDTO.builder()
				.storageType(StorageType.PERMANENT.toString())
				.path("/permanent")
				.bucket("permanent-bucket")
				.id(1L)
				.build();
	}

	@Test
	void testGetFile_Success() {
		// given
		Mockito.doNothing().when(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.when(resourceRepository.findById(MOCK_ID)).thenReturn(Optional.of(mockResourceEntity));
		Mockito.when(conversionService.convert(mockResourceEntity, ResourceDTO.class)).thenReturn(mockResourceDTO);

		// when
		ResourceDTO result = resourceService.getFile(MOCK_ID);

		// then
		Assertions.assertNotNull(result);
		Assertions.assertEquals(MOCK_ID, result.getId());
		Assertions.assertEquals(FILE_CONTENT, result.getData());

		Mockito.verify(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.verify(resourceRepository).findById(MOCK_ID);
		Mockito.verify(conversionService).convert(mockResourceEntity, ResourceDTO.class);
	}

	@Test
	void testGetFile_EntityNotFound() {
		// given
		Mockito.doNothing().when(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.when(resourceRepository.findById(MOCK_ID)).thenReturn(Optional.empty());

		// when/then
		EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> resourceService.getFile(MOCK_ID));

		Assertions.assertEquals(String.format(ResourceConstants.RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE, MOCK_ID), exception.getMessage());
		Mockito.verify(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.verify(resourceRepository).findById(MOCK_ID);
	}

	@Test
	void testSaveFileStage_Success() {
		Mockito.when(storageIntegrationService.getStorageByType(StorageType.STAGING)).thenReturn(mockStageStorageData);
		Mockito.when(s3Service.uploadFile(mockStageStorageData, FILE_CONTENT)).thenReturn(FILE_IDENTIFIER);
		Mockito.when(resourceRepository.save(any(ResourceEntity.class))).thenReturn(mockResourceEntity);

		// when
		Long result = resourceService.saveFileStage(FILE_CONTENT);

		// then
		Assertions.assertNotNull(result);
		Assertions.assertEquals(MOCK_ID, result);

		Mockito.verify(storageIntegrationService).getStorageByType(StorageType.STAGING);
		Mockito.verify(s3Service).uploadFile(mockStageStorageData, FILE_CONTENT);
		Mockito.verify(resourceRepository).save(any(ResourceEntity.class));
		Mockito.verify(resourceProducer).sendMessage(MOCK_ID);
	}

	@Test
	void testDeleteFiles_Success() {
		// given
		List<ResourceEntity> resourceEntities = MOCK_ID_LIST.stream()
				.map(this::createResourceEntity)
				.toList();

		Mockito.when(storageIntegrationService.getStorageByType(Mockito.eq(StorageType.PERMANENT))).thenReturn(mockPermanentStorageData);
		Mockito.doNothing().when(constraintsService).checkInlineStringIdsConstraints(MOCK_ID_LIST_STRING);
		Mockito.doReturn(resourceEntities).when(resourceRepository).findAllById(anyList());
		Mockito.doReturn(Boolean.TRUE).when(resourceRepository).existsById(anyLong());
		Mockito.when(s3Service.deleteFile(mockPermanentStorageData, FILE_IDENTIFIER)).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(resourceRepository).deleteAllById(anyList());
		Mockito.when(songIntegrationService.deleteMetadata(any(SongBatchRequestDTO.class))).thenReturn(Boolean.TRUE);
		Mockito.when(conversionService.convert(any(SongBatchRequestDTO.class), any(Class.class))).thenReturn(ResourceBatchDTO.builder().ids(MOCK_ID_LIST).build());

		// when
		ResourceBatchDTO result = resourceService.deleteFiles(MOCK_ID_LIST_STRING);

		// then
		Assertions.assertNotNull(result);
		Assertions.assertEquals(MOCK_ID_LIST, result.getIds());

		Mockito.verify(constraintsService).checkInlineStringIdsConstraints(MOCK_ID_LIST_STRING);
		Mockito.verify(resourceRepository).findAllById(anyList());
		Mockito.verify(s3Service, Mockito.times(3)).deleteFile(mockPermanentStorageData, FILE_IDENTIFIER);
		Mockito.verify(resourceRepository).deleteAllById(anyList());
		Mockito.verify(songIntegrationService).deleteMetadata(any(SongBatchRequestDTO.class));
	}

	@Test
	void testDeleteFiles_MetadataDeletionFails() {
		// given
		List<ResourceEntity> resourceEntities = MOCK_ID_LIST.stream()
				.map(this::createResourceEntity)
				.toList();

		Mockito.when(storageIntegrationService.getStorageByType(Mockito.eq(StorageType.PERMANENT))).thenReturn(mockPermanentStorageData);
		Mockito.doNothing().when(constraintsService).checkInlineStringIdsConstraints(MOCK_ID_LIST_STRING);
		Mockito.doReturn(resourceEntities).when(resourceRepository).findAllById(anyList());
		Mockito.when(s3Service.deleteFile(mockPermanentStorageData, FILE_IDENTIFIER)).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(resourceRepository).deleteAllById(anyList());
		Mockito.when(songIntegrationService.deleteMetadata(any(SongBatchRequestDTO.class))).thenReturn(Boolean.FALSE);

		// when/then
		RestClientException exception = Assertions.assertThrows(RestClientException.class, () -> resourceService.deleteFiles(MOCK_ID_LIST_STRING));

		Assertions.assertEquals(ResourceConstants.REST_DELETE_FILE_FAILED_MESSAGE, exception.getMessage());

		Mockito.verify(constraintsService).checkInlineStringIdsConstraints(MOCK_ID_LIST_STRING);
		Mockito.verify(resourceRepository).findAllById(anyList());
		Mockito.verify(s3Service, Mockito.times(3)).deleteFile(mockPermanentStorageData, FILE_IDENTIFIER);
		Mockito.verify(resourceRepository).deleteAllById(anyList());
		Mockito.verify(songIntegrationService).deleteMetadata(any(SongBatchRequestDTO.class));
	}

	@Test
	void testDeleteFiles_EmptyIds() {
		// given
		String emptyIds = "";
		Mockito.when(songIntegrationService.deleteMetadata(any(SongBatchRequestDTO.class))).thenReturn(Boolean.TRUE);
		Mockito.when(conversionService.convert(any(SongBatchRequestDTO.class), any(Class.class))).thenReturn(ResourceBatchDTO.builder().ids(List.of()).build());

		// when
		ResourceBatchDTO result = resourceService.deleteFiles(emptyIds);

		// then
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.getIds().isEmpty());

		Mockito.verifyNoInteractions(s3Service);
	}

	@Test
	void testDeleteFiles_InvalidIds() {
		// given
		Mockito.doThrow(new InvalidArgumentException("Invalid ID format"))
				.when(constraintsService).checkInlineStringIdsConstraints(MOCK_INVALID_ID_STRING);


		// when/then
		InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> resourceService.deleteFiles(MOCK_INVALID_ID_STRING));

		Assertions.assertEquals("Invalid ID format", exception.getMessage());

		Mockito.verify(constraintsService).checkInlineStringIdsConstraints(MOCK_INVALID_ID_STRING);
		Mockito.verifyNoInteractions(resourceRepository);
		Mockito.verifyNoInteractions(s3Service);
		Mockito.verifyNoInteractions(songIntegrationService);
	}

	@Test
	void testCheckExist_ResourceExists() {
		// given
		Mockito.doNothing().when(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.when(resourceRepository.existsById(MOCK_ID)).thenReturn(Boolean.TRUE);

		// when
		boolean result = resourceService.checkExist(MOCK_ID);

		// then
		Assertions.assertTrue(result);

		Mockito.verify(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.verify(resourceRepository).existsById(MOCK_ID);
	}

	@Test
	void testCheckExist_ResourceDoesNotExist() {
		// given

		Mockito.doNothing().when(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.when(resourceRepository.existsById(MOCK_ID)).thenReturn(Boolean.FALSE);

		// when/then
		EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> resourceService.checkExist(MOCK_ID));

		Assertions.assertEquals(String.format(ResourceConstants.RESOURCE_NOT_FOUND_MESSAGE_TEMPLATE, MOCK_ID), exception.getMessage());

		Mockito.verify(constraintsService).checkIdConstraints(MOCK_ID);
		Mockito.verify(resourceRepository).existsById(MOCK_ID);
	}

	private ResourceEntity createResourceEntity(Long id) {
		return ResourceEntity.builder()
				.id(id)
				.fileIdentifier(ResourceServiceUnitTest.FILE_IDENTIFIER)
				.build();
	}

	private ResourceDTO createResourceDTO(Long id) {
		return ResourceDTO.builder()
				.id(id)
				.data(ResourceServiceUnitTest.FILE_CONTENT)
				.build();
	}
}