package ca.corefacility.bioinformatics.irida.service.workflow.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowTestBuilder;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests for the IRIDA workflow service.
 * 
 *
 */
public class IridaWorkflowsServiceTest {

	private IridaWorkflow iridaWorkflow;
	private IridaWorkflow iridaWorkflow2;
	private IridaWorkflow iridaWorkflow3;

	private UUID iridaWorkflowId = UUID.fromString("807d1c7a-da99-4559-b8b2-b87ef647319c");
	private UUID iridaWorkflowId2 = UUID.fromString("6dc376af-b2fc-453f-b672-f790723b87e6");
	private UUID iridaWorkflowId3 = UUID.fromString("e77a3278-db7f-4bee-a022-e41f5de04a55");

	private IridaWorkflowSet iridaWorkflowSet;
	private IridaWorkflowIdSet defaultIridaWorkflowIdSet;

	private IridaWorkflowsService iridaWorkflowsService;

	@BeforeEach
	public void setup() throws IridaWorkflowException {
		iridaWorkflow = IridaWorkflowTestBuilder.buildTestWorkflow(iridaWorkflowId, IridaWorkflowTestBuilder.Input.SINGLE, "reference");
		iridaWorkflow2 = IridaWorkflowTestBuilder.buildTestWorkflow(iridaWorkflowId2, IridaWorkflowTestBuilder.Input.SINGLE, "reference");
		iridaWorkflow3 = IridaWorkflowTestBuilder.buildTestWorkflow(iridaWorkflowId3, IridaWorkflowTestBuilder.Input.SINGLE, "reference");

		iridaWorkflowSet = new IridaWorkflowSet(Sets.newHashSet(iridaWorkflow, iridaWorkflow2));
		defaultIridaWorkflowIdSet = new IridaWorkflowIdSet(Sets.newHashSet());

		iridaWorkflowsService = new IridaWorkflowsService(iridaWorkflowSet, defaultIridaWorkflowIdSet);
	}

	/**
	 * Tests successfully setting the default workflow.
	 * 
	 * @throws IridaWorkflowDefaultException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void setDefaultWorkflowSuccess() throws IridaWorkflowNotFoundException, IridaWorkflowDefaultException {
		iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId);
	}

	/**
	 * Tests failing to set a default workflow (non-existent workflow).
	 * 
	 * @throws IridaWorkflowDefaultException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void setDefaultWorkflowFail() throws IridaWorkflowNotFoundException, IridaWorkflowDefaultException {
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId3);
		});
	}

	/**
	 * Tests failing to set a default workflow (duplicate workflow).
	 * 
	 * @throws IridaWorkflowDefaultException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void setDefaultWorkflowFailDuplicate() throws IridaWorkflowNotFoundException, IridaWorkflowDefaultException {
		iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId);
		assertThrows(IridaWorkflowDefaultException.class, () -> {
			iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId);
		});
	}

	/**
	 * Tests successfully setting a set of default workflows.
	 * 
	 * @throws IridaWorkflowDefaultException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void setDefaultWorkflowsSuccess() throws IridaWorkflowNotFoundException, IridaWorkflowDefaultException {
		iridaWorkflowsService.setDefaultWorkflows(Sets.newHashSet(iridaWorkflowId));
	}

	/**
	 * Tests failing to set a set of default workflows.
	 * 
	 * @throws IridaWorkflowDefaultException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void setDefaultWorkflowsFailNotFound() throws IridaWorkflowNotFoundException, IridaWorkflowDefaultException {
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.setDefaultWorkflows(Sets.newHashSet(iridaWorkflowId, iridaWorkflowId3));
		});
	}

	/**
	 * Tests failing to set a set of default workflows (multiple workflows of
	 * the same type).
	 * 
	 * @throws IridaWorkflowDefaultException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void setDefaultWorkflowsFail() throws IridaWorkflowNotFoundException, IridaWorkflowDefaultException {
		assertThrows(IridaWorkflowDefaultException.class, () -> {
			iridaWorkflowsService.setDefaultWorkflows(Sets.newHashSet(iridaWorkflowId, iridaWorkflowId2));
		});
	}

	/**
	 * Tests successfully registering a workflow
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testRegisterWorkflowSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(iridaWorkflow3);
	}

	/**
	 * Tests failing to register a workflow
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testRegisterWorkflowFailDuplicate() throws IridaWorkflowException {
		assertThrows(IridaWorkflowException.class, () -> {
			iridaWorkflowsService.registerWorkflow(iridaWorkflow);
		});
	}

	/**
	 * Tests successfully registering a set of workflows
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testRegisterWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(iridaWorkflow3));
	}

	/**
	 * Tests failing to register a set of workflows
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testRegisterWorkflowsFail() throws IridaWorkflowException {
		assertThrows(IridaWorkflowException.class, () -> {
			iridaWorkflowsService.registerWorkflows(Sets.newHashSet(iridaWorkflow));
		});
	}

	/**
	 * Tests getting a default workflow
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetDefaultWorkflowByType() throws IridaWorkflowException {
		iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId);
		assertEquals(iridaWorkflow, iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.DEFAULT));
	}

	/**
	 * Tests getting a default workflow and failing
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetDefaultWorkflowByTypeFail() throws IridaWorkflowException {
		iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId);
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.PHYLOGENOMICS);
		});
	}

	/**
	 * Tests getting all default workflows by type
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllDefaultWorkflowsByType() throws IridaWorkflowException {
		iridaWorkflowsService.setDefaultWorkflow(iridaWorkflowId);
		Map<AnalysisType, IridaWorkflow> defaultWorkflowsMap = iridaWorkflowsService.getAllDefaultWorkflowsByType(Sets
				.newHashSet(BuiltInAnalysisTypes.DEFAULT));
		assertEquals(ImmutableMap.of(BuiltInAnalysisTypes.DEFAULT, iridaWorkflow), defaultWorkflowsMap,
				"invalid type of workflow");
	}

	/**
	 * Tests successfully getting a set of workflows by a type.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsByTypeSuccess() throws IridaWorkflowException {
		assertEquals(Sets.newHashSet(iridaWorkflow, iridaWorkflow2),
				iridaWorkflowsService.getAllWorkflowsByType(BuiltInAnalysisTypes.DEFAULT),
				"invalid workflows by type " + BuiltInAnalysisTypes.DEFAULT);
	}

	/**
	 * Tests failing to get a set of workflows by a type (no workflows).
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsByTypeFail() throws IridaWorkflowException {
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getAllWorkflowsByType(BuiltInAnalysisTypes.PHYLOGENOMICS);
		});
	}

	/**
	 * Tests successfully getting a set of workflow types.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetRegisteredWorkflowTypes() throws IridaWorkflowException {
		assertEquals(Sets.newHashSet(BuiltInAnalysisTypes.DEFAULT), iridaWorkflowsService.getRegisteredWorkflowTypes(), "invalid registered workflow types");
	}

	/**
	 * Tests successfully getting a workflow by an id.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetIridaWorkflowSuccess() throws IridaWorkflowException {
		assertEquals(iridaWorkflow, iridaWorkflowsService.getIridaWorkflow(iridaWorkflowId), "invalid workflow");
	}

	/**
	 * Tests failing to get a workflow by an id.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetIridaWorkflowFail() throws IridaWorkflowException {
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getIridaWorkflow(iridaWorkflowId3);
		});
	}

	/**
	 * Tests successfully getting a set of registered workflows.
	 */
	@Test
	public void testGetRegisteredWorkflows() {
		assertEquals(Sets.newHashSet(iridaWorkflow, iridaWorkflow2), iridaWorkflowsService.getRegisteredWorkflows(), "invalid registered workflows");
	}
}
