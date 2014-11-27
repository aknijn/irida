package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class IridaWorkflowsService {
	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsService.class);

	private static final String WORKFLOWS_DIR = "workflows";

	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	/**
	 * Stores registered workflows within IRIDA.
	 */
	private Map<UUID, IridaWorkflow> registeredWorkflows;

	/**
	 * Builds a new {@link IridaWorkflowService} for loading up installed
	 * workflows.
	 * 
	 * @param iridaWorkflowLoaderService
	 *            The service used to load up workflows.
	 */
	@Autowired
	public IridaWorkflowsService(IridaWorkflowLoaderService iridaWorkflowLoaderService) {
		this.iridaWorkflowLoaderService = iridaWorkflowLoaderService;

		registeredWorkflows = new HashMap<>();
	}

	/**
	 * Registers workflows that are stored as resources belonging to the passed
	 * Analysis class.
	 * 
	 * @param analysisClass
	 *            The class defining the type of analysis.
	 * @throws IOException
	 *             If there was a problem reading a workflow.
	 * @throws IridaWorkflowLoadException
	 *             If there was a problem loading a workflow.
	 */
	public void registerAnalysis(Class<? extends Analysis> analysisClass) throws IOException,
			IridaWorkflowLoadException {
		checkNotNull(analysisClass, "analysisClass is null");

		logger.debug("Registering Analysis: " + analysisClass);
		String analysisName = analysisClass.getSimpleName();
		Path workflowsResourcePath = Paths.get(analysisClass.getResource(WORKFLOWS_DIR).getFile());
		Path workflowPath = workflowsResourcePath.resolve(analysisName);

		if (!Files.isDirectory(workflowPath)) {
			throw new IridaWorkflowLoadException("Missing directory " + workflowPath + " for class " + analysisClass);
		} else {
			try {
				Set<IridaWorkflow> workflowVersions = iridaWorkflowLoaderService
						.loadAllWorkflowImplementations(workflowPath);

				for (IridaWorkflow workflow : workflowVersions) {
					if (registeredWorkflows.containsKey(workflow.getWorkflowIdentifier())) {
						throw new IridaWorkflowLoadException("Duplicate workflow " + workflow.getWorkflowIdentifier());
					} else {
						registeredWorkflows.put(workflow.getWorkflowIdentifier(), workflow);
					}
				}
			} catch (Exception e) {
				throw new IridaWorkflowLoadException("Could not load workflows from directory " + workflowPath, e);
			}
		}
	}

	/**
	 * Returns a workflow with the given id.
	 * 
	 * @param workflowId
	 *            The identifier of the workflow to get.
	 * @return An {@link IridaWorkflow} with the given identifier.
	 * @throws IridaWorkflowNotFoundException
	 *             If no workflow with the given identifier was found.
	 */
	public IridaWorkflow getIridaWorkflow(UUID workflowId) throws IridaWorkflowNotFoundException {
		checkNotNull(workflowId, "workflowId is null");

		if (registeredWorkflows.containsKey(workflowId)) {
			return registeredWorkflows.get(workflowId);
		} else {
			throw new IridaWorkflowNotFoundException(workflowId);
		}
	}

	/**
	 * Gets a Collection of all installed workflows.
	 * 
	 * @return A collection of all installed workflows.
	 */
	public Collection<IridaWorkflow> getInstalledWorkflows() {
		return registeredWorkflows.values();
	}
}
