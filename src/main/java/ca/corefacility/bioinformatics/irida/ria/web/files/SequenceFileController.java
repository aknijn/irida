package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

/**
 * Controller for all {@link SequenceFile} related views
 */
@Controller
public class SequenceFileController {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileController.class);
	/*
	 * PAGES
	 */
	public static final String BASE_URL = "sequenceFiles/";
	public static final String FASTQC_PAGE = BASE_URL + "fastqc";

	/*
	 * SERVICES
	 */
	private SequencingObjectService sequencingObjectService;

	@Autowired
	public SequenceFileController(SequencingObjectService sequencingObjectService) {
		this.sequencingObjectService = sequencingObjectService;
	}

	/**
	 * Gets the name of the template for the sequence file chart / main page.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @param sampleId           Optional sample id depending on the request mapping
	 * @param projectId          Optional project id depending on the request mapping
	 * @param runId              Optional run id depending on the request mapping
	 * @return The name of the template.
	 */
	@RequestMapping(value = { "/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/summary/**",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/**",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/summary/**",
			"/sequencingRuns/{runId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/summary/**" })
	public String getSequenceFilePage(@PathVariable Long sequencingObjectId, @PathVariable Long sequenceFileId,
			@PathVariable(required = false) Long sampleId, @PathVariable(required = false) Long projectId,
			@PathVariable(required = false) Long runId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		return FASTQC_PAGE;
	}

	/**
	 * Downloads a sequence file.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @param response           {@link HttpServletResponse}
	 * @throws IOException if we can't write the file to the response.
	 */
	@RequestMapping("/sequenceFiles/download/{sequencingObjectId}/file/{sequenceFileId}")
	public void downloadSequenceFile(@PathVariable Long sequencingObjectId, @PathVariable Long sequenceFileId,
			HttpServletResponse response) throws IOException {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile sequenceFile = sequencingObject.getFileWithId(sequenceFileId);
		Path path = sequenceFile.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sequenceFile.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

}
