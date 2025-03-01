package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

@Tag("IntegrationTest") @Tag("Service")
@SpringBootTest
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionRepositoryIT {

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private ReferenceFileRepository referenceFileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SequencingObjectRepository objectRepository;

	private UUID workflowId = UUID.randomUUID();

	private AnalysisSubmission analysisSubmission;
	private AnalysisSubmission analysisSubmission2;
	private AnalysisSubmission analysisSubmission2b;
	private SequenceFilePair sequenceFilePair;
	private SequenceFile sequenceFile;
	private ReferenceFile referenceFile;
	private ReferenceFile referenceFile2;
	private SingleEndSequenceFile singleEndFile;

	private static final String analysisId = "10";
	private static final String analysisId2 = "11";
	private final String analysisName = "analysis 1";
	private final String analysisName2 = "analysis 2";

	private User submitter1;
	private User submitter2;

	/**
	 * Sets up objects for test.
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
		singleEndFile = (SingleEndSequenceFile) objectRepository.findById(2L).orElse(null);
		sequenceFile = singleEndFile.getFileWithId(1L);
		assertNotNull(sequenceFile);
		Set<SequencingObject> singleFiles = Sets.newHashSet(singleEndFile);

		SingleEndSequenceFile singleEndFile2 = (SingleEndSequenceFile) objectRepository.findById(3L).orElse(null);
		SequenceFile sequenceFile2 = singleEndFile2.getFileWithId(2L);
		assertNotNull(sequenceFile2);
		Set<SequencingObject> singleFiles2 = Sets.newHashSet(singleEndFile2);

		sequenceFilePair = (SequenceFilePair) objectRepository.findById(1L).orElse(null);
		assertNotNull(sequenceFilePair);

		referenceFile = referenceFileRepository.findById(1L).orElse(null);
		assertNotNull(referenceFile);
		
		referenceFile2 = referenceFileRepository.findById(2L).orElse(null);

		submitter1 = userRepository.findById(1L).orElse(null);
		submitter2 = userRepository.findById(2L).orElse(null);

		analysisSubmission = AnalysisSubmission.builder(workflowId).name(analysisName).inputFiles(singleFiles)
				.referenceFile(referenceFile).build();
		analysisSubmission.setRemoteAnalysisId(analysisId);
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission.setSubmitter(submitter1);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);

		analysisSubmission2 = AnalysisSubmission.builder(workflowId).name(analysisName2)
				.inputFiles(singleFiles2).referenceFile(referenceFile).build();
		analysisSubmission2.setRemoteAnalysisId(analysisId2);
		analysisSubmission2.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission2.setSubmitter(submitter2);
		analysisSubmission2.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);
		
		analysisSubmission2b = AnalysisSubmission.builder(workflowId).name(analysisName2)
				.inputFiles(singleFiles2).referenceFile(referenceFile2).build();
		analysisSubmission2b.setRemoteAnalysisId(analysisId2);
		analysisSubmission2b.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission2b.setSubmitter(submitter2);
		analysisSubmission2b.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);
	}

	/**
	 * Tests getting a single analysis by its state and succeeding.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateSuccess() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.SUBMITTING);
		assertEquals(1, submittedAnalyses.size());
		assertEquals(analysisId, submittedAnalyses.get(0).getRemoteAnalysisId());
	}

	/**
	 * Tests getting multiple analyses by a state and succeeding.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateMultiple() {
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.SUBMITTING);
		assertEquals(2, submittedAnalyses.size());
		Set<String> analysisIdsSet = Sets.newHashSet(submittedAnalyses.get(0).getRemoteAnalysisId(), submittedAnalyses
				.get(1).getRemoteAnalysisId());
		assertEquals(Sets.newHashSet(analysisId, analysisId2), analysisIdsSet, "invalid ids of returned submissions");
	}

	/**
	 * Tests finding no analyses by the given state.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateNone() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.RUNNING);
		assertEquals(Collections.EMPTY_LIST, submittedAnalyses);
	}

	/**
	 * Tests getting a single analysis by two states and succeeding
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisTwoStateSuccess() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository.findByAnalysisState(
				AnalysisState.SUBMITTING, AnalysisCleanedState.NOT_CLEANED);
		assertEquals(1, submittedAnalyses.size(), "Invalid size of returned analyses");
		assertEquals(analysisId, submittedAnalyses.get(0).getRemoteAnalysisId(), "invalid ids of returned submissions");
	}

	/**
	 * Tests getting multiple analyses by two states and succeeding
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisTwoStateMultiple() {
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository.findByAnalysisState(
				AnalysisState.SUBMITTING, AnalysisCleanedState.NOT_CLEANED);
		assertEquals(2, submittedAnalyses.size(), "Invalid size of returned analyses");
		Set<String> analysisIdsSet = Sets.newHashSet(submittedAnalyses.get(0).getRemoteAnalysisId(), submittedAnalyses
				.get(1).getRemoteAnalysisId());
		assertEquals(Sets.newHashSet(analysisId, analysisId2), analysisIdsSet, "invalid ids of returned submissions");
	}

	/**
	 * Tests not getting any analyses by two states.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisTwoStateNone() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository.findByAnalysisState(
				AnalysisState.SUBMITTING, AnalysisCleanedState.CLEANED);
		assertEquals(0, submittedAnalyses.size(), "Invalid size of returned analyses");
	}

	/**
	 * Tests creating an analysis with only paired files, no reference
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisPaired() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder(workflowId)
				.name("submission paired 1").inputFiles(Sets.newHashSet(sequenceFilePair)).build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		assertEquals(Sets.newHashSet(sequenceFilePair),
				objectRepository.findSequencingObjectsForAnalysisSubmission(savedSubmission));
		assertFalse(savedSubmission.getReferenceFile().isPresent());
	}

	/**
	 * Tests creating an analysis with only paired files, with reference
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisPairedReference() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder(workflowId)
				.name("submission paired 1").inputFiles(Sets.newHashSet(sequenceFilePair))
				.referenceFile(referenceFile).build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		assertEquals(Sets.newHashSet(sequenceFilePair),
				objectRepository.findSequencingObjectsForAnalysisSubmission(savedSubmission));
		assertEquals(referenceFile, savedSubmission.getReferenceFile().get());
	}
	
	/**
	 * Tests creating an analysis with both single and paired files and a
	 * reference genome.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisSingleAndPairedAndReference() {
		Set<SequencingObject> inputs = Sets.newHashSet(sequenceFilePair, singleEndFile);
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder(workflowId).name("submission paired 1")
				.inputFiles(inputs).referenceFile(referenceFile).build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		Set<SequencingObject> inputsSaved = objectRepository
				.findSequencingObjectsForAnalysisSubmission(savedSubmission);

		assertTrue(inputsSaved.contains(singleEndFile), "should have single end file in collection");
		assertTrue(inputsSaved.contains(sequenceFilePair), "should have pair file in collection");

		assertEquals(referenceFile, savedSubmission.getReferenceFile().get());
	}

	/**
	 * Tests creating an analysis with both single and paired files and no
	 * reference genome.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisSingleAndPaired() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder(workflowId).name("submission paired 1")
				.inputFiles(Sets.newHashSet(sequenceFilePair, singleEndFile)).build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		Set<SequencingObject> inputsSaved = objectRepository
				.findSequencingObjectsForAnalysisSubmission(savedSubmission);

		assertTrue(inputsSaved.contains(singleEndFile), "should have single end file in collection");
		assertTrue(inputsSaved.contains(sequenceFilePair), "should have pair file in collection");

		assertFalse(savedSubmission.getReferenceFile().isPresent());
	}


	/**
	 * Tests creating an analysis with no set submitter and failing.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisNoSubmitterFail() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder(workflowId)
				.name("submission paired 1").inputFiles(Sets.newHashSet(sequenceFilePair)).build();
		assertThrows(DataIntegrityViolationException.class, () -> {
			analysisSubmissionRepository.save(analysisSubmissionPaired);
		});
	}

	/**
	 * Tests successfully finding an analysis submission by the submitter.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindBySubmitterSuccess() {
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmission);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findBySubmitter(submitter1);
		assertNotNull(submissions, "submissions should not be null");
		assertEquals(1, submissions.size(), "there are an invalid number of submissions found");
		AnalysisSubmission returnedSubmission = submissions.iterator().next();
		assertEquals(savedSubmission.getId(), returnedSubmission.getId(), "the id of the submission returned is incorrect");
		assertEquals(savedSubmission.getSubmitter().getId(), submitter1.getId(), "the submitter of the submission returned is incorrect");
	}

	/**
	 * Tests successfully finding multiple analyses submissions by the
	 * submitter.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindBySubmitterMultipleSuccess() {
		analysisSubmission2.setSubmitter(submitter1);
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findBySubmitter(submitter1);
		assertEquals(2, submissions.size(), "there are an invalid number of submissions found");
	}

	/**
	 * Tests failing to find an analysis submission by the submitter.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindBySubmitterFail() {
		analysisSubmissionRepository.save(analysisSubmission);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findBySubmitter(submitter2);
		assertEquals(0, submissions.size(), "there should be no submissions found");
	}
	
	/**
	 * Tests successfully getting analysis submissions by a reference file.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByReferenceFileSuccess() {
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findByReferenceFile(referenceFile);
		assertEquals(2, submissions.size(), "should have gotten 2 analysis submissions");
	}
	
	/**
	 * Tests successfully getting analysis submissions by a reference file.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByReferenceFileSuccess2() {
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2b);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findByReferenceFile(referenceFile);
		assertEquals(1, submissions.size(), "should have gotten 1 analysis submissions");
	}

	/**
	 * Tests successfully getting analysis submissions by a reference file.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByReferenceFileNone() {
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findByReferenceFile(referenceFile2);
		assertEquals(0, submissions.size(), "should have gotten 0 analysis submissions");
	}
}
