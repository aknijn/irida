package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Locale;

//ISS
import ca.corefacility.bioinformatics.irida.util.SEU;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * UI Service to handle samples within a project.
 */
@Component
public class UIProjectSampleService {

	private static final Logger logger = LoggerFactory.getLogger(UIProjectSampleService.class);

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectSampleService(ProjectService projectService, SampleService sampleService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
	}

	/**
	 * Validate a sample name to ensure can be stored correctly.  Must be:
	 * - at least 3 characters long,
	 * - no special characters (including spaces)
	 * - name must not already exist for a sample in the project
	 *
	 * @param name      Name to validate.
	 * @param projectId current project identifier
	 * @param locale    current users locale
	 * @return result of the validation.
	 */
	public ResponseEntity<SampleNameValidationResponse> validateNewSampleName(String name, long projectId, Locale locale) {
		int SAMPLE_NAME_MIN_LENGTH = 3;

		Project project = projectService.read(projectId);

		// Make sure it has the correct length
		if (name.length() <= SAMPLE_NAME_MIN_LENGTH) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
					.body(new SampleNameValidationResponse("error", messageSource.getMessage("server.AddSample.error.length", new Object[] {}, locale)));
		}

		/*
		This is copied from the previous client side validation.
		 */
		if (!name.matches("[A-Za-z\\d-_!@#$%~`]+")) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
					.body(new SampleNameValidationResponse("error", messageSource.getMessage("server.AddSample.error.special.characters", new Object[] {}, locale)));
		}

		// Check to see if the sample name already exists.
		try {
			sampleService.getSampleBySampleName(project, name);
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new SampleNameValidationResponse("error",
							messageSource.getMessage("server.AddSample.error.exists", new Object[] {}, locale)));

		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok(new SampleNameValidationResponse("success", null));
		}

	}

	/**
	 * Create a new sample in a project
	 *
	 * @param request   {@link CreateSampleRequest} details about the sample to create
	 * @param projectId Identifier for the current project
	 * @param locale Users current locale
	 * @return result of creating the sample
	 */
	public ResponseEntity<AjaxResponse> createSample(CreateSampleRequest request, long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		try {
			Sample sample = new Sample(request.getName());
			if (!Strings.isNullOrEmpty(request.getOrganism())) {
				sample.setOrganism(request.getOrganism());
			}
			else { // ISS force project organism
				sample.setOrganism(project.getOrganism());
			}
			//ISS collegamento con SEU
			try {
				if (sample.getOrganism().equals("Shiga toxin-producing Escherichia coli")) {
					logger.debug("Adding information from SEU database");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SEU seu = new SEU();
					Map<String, String> SEUmap = seu.getData(sample.getSampleName());
					if (SEUmap.get("DataEsordio") != null) { sample.setCollectionDate(sdf.parse(SEUmap.get("DataEsordio"))); }
					if (SEUmap.get("Ospedale") != null) { sample.setCollectedBy(SEUmap.get("Ospedale")); logger.debug("Ospedale: " + SEUmap.get("Ospedale"));}
					if (SEUmap.get("Regione") != null) { sample.setGeographicLocationName(SEUmap.get("Regione")); }
					if (SEUmap.get("Provincia") != null) { sample.setGeographicLocationName2(SEUmap.get("Provincia")); }
					if (SEUmap.get("Comune") != null) { sample.setGeographicLocationName3(SEUmap.get("Comune")); }
					else { if (SEUmap.get("Localita") != null) { sample.setGeographicLocationName3(SEUmap.get("Localita")); } }
				}
			} catch (SQLException ex) {
				logger.warn("Attempt to connect to SQL database failed", ex);
			} catch (ParseException ex) {
				logger.warn("Attempt to parse DataEsordio failed", ex);
			}
			Join<Project, Sample> join = projectService.addSampleToProject(project, sample, true);
			return ResponseEntity.ok(new AjaxCreateItemSuccessResponse(join.getObject()
					.getId()));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok(new AjaxErrorResponse(
					messageSource.getMessage("server.AddSample.error.exists", new Object[] {}, locale)));
		}
	}
}
