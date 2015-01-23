package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.components.SubmissionIds;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Controller for pipeline related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@Scope("session")
@RequestMapping(PipelineController.BASE_URL)
public class PipelineController extends BaseController {
	// URI's
	public static final String BASE_URL = "/pipelines";
	/*
	 * CONSTANTS
	 */
	public static final String URL_EMPTY_CART_REDIRECT = "redirect:/pipelines";
	public static final String URL_LAUNCH ="pipelines/pipeline_selection";
	public static final String URL_PHYLOGENOMICS = "pipelines/types/phylogenomics";
	public static final String URI_LIST_PIPELINES = "/ajax/list.json";
	public static final String URI_AJAX_START_PIPELINE = "/ajax/start.json";
	public static final String URI_AJAX_CART_LIST = "/ajax/cart_list.json";
	// JSON KEYS
	public static final String JSON_KEY_SAMPLE_ID = "id";
	public static final String JSON_KEY_SAMPLE_OMIT_FILES_LIST = "omit";
	private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
	/*
	 * Converters
	 */
	private Formatter<Date> dateFormatter;
	/*
	 * SERVICES
	 */
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private SequenceFilePairService sequenceFilePairService;
	private AnalysisSubmissionService analysisSubmissionService;
	private ProjectService projectService;
	private UserService userService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;
	/*
	 * CONTROLLERS
	 */
	private CartController cartController;

	@Autowired
	public PipelineController(SequenceFileService sequenceFileService,
			SequenceFilePairService sequenceFilePairService,
			ReferenceFileService referenceFileService,
			AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService,
			ProjectService projectService,
			UserService userService,
			CartController cartController,
			MessageSource messageSource) {
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.projectService = projectService;
		this.userService = userService;
		this.cartController = cartController;
		this.messageSource = messageSource;
		this.dateFormatter = new DateFormatter();
	}

	/**
	 * Get the Pipeline Selection Page
	 *
	 * @param model
	 * 		{@link Model}
	 * @param locale
	 * 		Current users {@link Locale}
	 *
	 * @return location of the pipeline selection page.
	 */
	@RequestMapping
	public String getPipelineLaunchPage(final Model model, Locale locale) {
		Set<AnalysisType> workflows = workflowsService.getRegisteredWorkflowTypes();

		List<Map<String, String>> flows = new ArrayList<>(workflows.size());
		workflows.stream().forEach(type -> {
			IridaWorkflow flow = null;
			try {
				flow = workflowsService.getDefaultWorkflowByType(type);
			IridaWorkflowDescription description = flow.getWorkflowDescription();
			String name = type.toString();
			String key = "workflow." + name;
			flows.add(ImmutableMap.of(
					"name", name,
					"id", description.getId().toString(),
					"title",
					messageSource
							.getMessage(key + ".title", new Object[] { }, locale),
					"description",
					messageSource
							.getMessage(key + ".description", new Object[] { }, locale)
			));
			} catch (IridaWorkflowNotFoundException e) {
				logger.error("Workflow not found - See stack:", e.getMessage());
			}
		});
		model.addAttribute("counts", getCartSummaryMap());
		model.addAttribute("workflows", flows);
		return URL_LAUNCH;
	}

	@RequestMapping(value = "/phylogenomics/{pipelineId}")
	public String getPhylogenomicsPage(final Model model, Principal principal, Locale locale, @PathVariable UUID pipelineId) {
		String response = URL_EMPTY_CART_REDIRECT;

		Map<Project, Set<Sample>> cartMap = cartController.getSelected();
		// Cannot run a pipeline on an empty cart!
		if (!cartMap.isEmpty()) {

			IridaWorkflow iridaWorkflow = null;
			try {
				iridaWorkflow = workflowsService.getIridaWorkflow(pipelineId);
			} catch (IridaWorkflowNotFoundException e) {
				logger.error("Workflow not found - See stack:", e.getMessage());
			}

			User user = userService.getUserByUsername(principal.getName());
			// Get all the reference files that could be used for this pipeline.
			List<Map<String, Object>> referenceFileList = new ArrayList<>();
			List<Map<String, Object>> projectList = new ArrayList<>();
			List<Map<String, Object>> addRefList = new ArrayList<>();
			for (Project project : cartMap.keySet()) {
				List<Join<Project, ReferenceFile>> joinList = referenceFileService.getReferenceFilesForProject(project);
				for (Join<Project, ReferenceFile> join : joinList) {
					referenceFileList.add(ImmutableMap.of(
							"project", project,
							"file", join.getObject()
					));
				}

				if (referenceFileList.size() == 0) {
					if (user.getSystemRole().equals(Role.ROLE_ADMIN) || projectService
							.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER)) {
						addRefList.add(ImmutableMap.of(
								"name", project.getLabel(),
								"id", project.getId()
						));
					}
				}

				Set<Sample> samples = cartMap.get(project);
				Map<String, Object> projectMap = new HashMap<>();
				List<Map<String, Object>> sampleList = new ArrayList<>();
				for (Sample sample : samples) {
					Map<String, Object> sampleMap = new HashMap<>();
					sampleMap.put("name", sample.getLabel());
					sampleMap.put("id", sample.getId().toString());
					List<Map<String, Object>> fileList = new ArrayList<>();

					// Paired end reads
					List<SequenceFilePair> sequenceFilePairs = sequenceFilePairService
							.getSequenceFilePairsForSample(sample);
					for (SequenceFilePair pair : sequenceFilePairs) {
						fileList.add(ImmutableMap.of(
								"id", pair.getId(),
								"type", "paired_end",
								"files", pair.getFiles(),
								"createdDate", pair.getCreatedDate()
						));
					}

					// Singe end reads
					List<Join<Sample, SequenceFile>> sfJoin = sequenceFileService.getSequenceFilesForSample(sample);
					for (Join<Sample, SequenceFile> join : sfJoin) {
						SequenceFile file = join.getObject();
						fileList.add(ImmutableMap.of(
								"type", "single_end",
								"id", file.getId(),
								"label", file.getLabel(),
								"createdDate", file.getCreatedDate()
						));
					}

					sampleMap.put("files", fileList);
					sampleList.add(sampleMap);
				}

				projectMap.put("id", project.getId().toString());
				projectMap.put("name", project.getLabel());
				projectMap.put("samples", sampleList);
				projectList.add(projectMap);
			}
			model.addAttribute("name", iridaWorkflow.getWorkflowDescription().getName() + " (" + dateFormatter.print(new Date(), locale) + ")");
			model.addAttribute("pipelienId", pipelineId.toString());
			model.addAttribute("referenceFiles", referenceFileList);
			model.addAttribute("addRefProjects", addRefList);
			model.addAttribute("projects", projectList);
			response = URL_PHYLOGENOMICS;
		}

		return response;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Launch a phylogenomics pipeline
	 * @param session the current {@link HttpSession}
	 * @param pipelineId the id for the {@link IridaWorkflow}
	 * @param single a list of {@link SequenceFile} id's
	 * @param paired a list of {@link SequenceFilePair} id's
	 * @param ref the id for a {@link ReferenceFile}
	 * @param name a user provided name for the {@link IridaWorkflow}
	 * @return a JSON response with the status and any messages.
	 */
	@RequestMapping(value = "/ajax/start/{pipelineId}", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> ajaxStartPipelinePhylogenomics(HttpSession session, Locale locale,
			@PathVariable UUID pipelineId,
			@RequestParam(value = "single[]", required = false) List<Long> single, @RequestParam(value = "paired[]", required = false) List<Long> paired,
			@RequestParam Long ref, @RequestParam String name) {
		Map<String, Object> result;

		if (Strings.isNullOrEmpty(name)) {
			result = ImmutableMap
					.of("error", messageSource.getMessage("workflow.no-name-provided", new Object[] { }, locale));
		} else {
			List<SequenceFile> sequenceFiles = new ArrayList<>();
			List<SequenceFilePair> sequenceFilePairs = new ArrayList<>();

			if (single != null) {
				sequenceFiles = (List<SequenceFile>) sequenceFileService.readMultiple(single);
			}

			if (paired != null) {
				sequenceFilePairs = (List<SequenceFilePair>) sequenceFilePairService.readMultiple(paired);
			}

			ReferenceFile referenceFile = referenceFileService.read(ref);
			AnalysisSubmission analysisSubmission;

			if (sequenceFiles.size() > 0 && sequenceFilePairs.size() > 0) {
				analysisSubmission = AnalysisSubmission
						.createSubmissionSingleAndPairedReference(name, new HashSet<>(sequenceFiles),
								new HashSet<>(sequenceFilePairs), referenceFile,
								pipelineId);
			}
			else if (sequenceFiles.size() > 0 && sequenceFilePairs.size() == 0) {
				analysisSubmission = AnalysisSubmission
						.createSubmissionSingleReference(name, new HashSet<>(sequenceFiles), referenceFile,
						pipelineId);
			}
			else {
				analysisSubmission = AnalysisSubmission
						.createSubmissionPairedReference(name, new HashSet<>(sequenceFilePairs),
						referenceFile, pipelineId);
			}

			AnalysisSubmission submission = analysisSubmissionService.create(analysisSubmission);

			// TODO [15-01-21] (Josh): This should be replaced by storing the values into the database.
			SubmissionIds submissionIds = (SubmissionIds) session.getAttribute("submissionIds");
			if (submissionIds == null) {
				submissionIds = new SubmissionIds();
			}
			submissionIds.addId(submission.getId());
			session.setAttribute("submissionIds", submissionIds);

			result = ImmutableMap.of("result", "success", "submissionId", submission.getId());
		}

		return result;
	}

	/**
	 * Get details about the contents of the cart.
	 *
	 * @return {@link Map} containing the counts of the projects and samples in the cart.
	 */
	private Map<String, Integer> getCartSummaryMap() {
		return ImmutableMap.of(
				"projects", cartController.getNumberOfProjects(),
				"samples", cartController.getNumberOfSamples()
		);
	}
}
