package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.ReferenceFileAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectReferenceFileService;

import com.github.jsonldjava.shaded.com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ReferenceFileAjaxControllerTest {

	public static final Long FILE_ID = 1L;
	public static final String FILE_NAME = "test_file.fasta";
	public static final String FILE_PATH = "src/test/resources/files/test_file.fasta";
	public static final Long PROJECT_ID = 11L;

	// Controller
	private ReferenceFileAjaxController controller;

	// Services
	private UIProjectReferenceFileService uiProjectReferenceFileService;

	private List<MultipartFile> mockMultipartFiles;

	@Before
	public void setUp() throws IOException {
		uiProjectReferenceFileService = mock(UIProjectReferenceFileService.class);

		// Set up the reference file
		controller = new ReferenceFileAjaxController(uiProjectReferenceFileService);

		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		mockMultipartFiles = ImmutableList
				.of(new MockMultipartFile(FILE_NAME, FILE_NAME, "octet-stream", origBytes));
	}

	@Test
	public void testCreateNewReferenceFile() throws UnsupportedReferenceFileContentError, IOException {
		ResponseEntity<AjaxResponse> response = controller.addReferenceFileToProject(FILE_ID, mockMultipartFiles, Locale.ENGLISH);
		assertEquals("Receive an 200 OK response", response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testDeleteReferenceFile() {
		ResponseEntity<AjaxResponse> response = controller.deleteReferenceFile(FILE_ID, PROJECT_ID, Locale.ENGLISH);
		assertEquals("Receive an 200 OK response", response.getStatusCode(), HttpStatus.OK);
	}
}
