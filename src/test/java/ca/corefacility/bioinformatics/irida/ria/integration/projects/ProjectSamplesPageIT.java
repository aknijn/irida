package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

import static org.junit.Assert.*;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectSamplesPageIT extends AbstractIridaUIITChromeDriver {
	@After
	public void resetTable() {
		/*
		This was added to ensure that after every test the samples table is returned to its default
		state.  Current DataTables stores a reference to which page in the table the user is on.
		 */
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.closeModalIfOpen();
		page.selectPaginationPage(1);
	}

	@Test(expected = AssertionError.class)
	public void testGoingToInvalidPage() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage.gotToPage(driver(), 100);
	}

	@Test
	public void testPageSetUp() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertTrue("Should have the project name as the page main header.", page.getActivePage().equals("Samples"));
		assertEquals("Should display 10 projects initially.", 10, page.getNumberProjectsDisplayed());
	}

	@Test
	public void testToolbarButtonsAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertFalse("Sample Tools should be hidden from a collaborator", page.isSampleToolsAvailable());
	}

	@Test
	public void testToolbarButtonsAsManager() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Test set up with no sample selected
		page.openToolsDropDown();
		assertFalse("Merge option should not be enabled", page.isMergeBtnEnabled());
		assertFalse("Remove option should not be enabled", page.isRemoveBtnEnabled());
		page.closeToolsDropdown();
		page.openExportDropdown();
		assertFalse("Download option should not be enabled", page.isDownloadBtnEnabled());
		assertFalse("NCBI Export option should not be enabled", page.isNcbiBtnEnabled());

		// Test with one sample selected
		page.selectSample(0);
		page.openToolsDropDown();
		assertFalse("Merge option should not be enabled", page.isMergeBtnEnabled());
		assertTrue("Remove option should be enabled", page.isRemoveBtnEnabled());
		page.closeToolsDropdown();
		page.openExportDropdown();
		assertTrue("Download option should be enabled", page.isDownloadBtnEnabled());
		assertTrue("NCBI Export option should be enabled", page.isNcbiBtnEnabled());

		// Test with two samples selected
		page.selectSample(1);
		page.openToolsDropDown();
		assertTrue("Merge option should be enabled", page.isMergeBtnEnabled());
		assertTrue("Share option should be enabled", page.isShareBtnEnabled());
		assertTrue("Remove option should be enabled", page.isRemoveBtnEnabled());
		page.openExportDropdown();
		assertTrue("Download option should be enabled", page.isDownloadBtnEnabled());
		assertTrue("NCBI Export option should be enabled", page.isNcbiBtnEnabled());
	}

	@Test
	public void testPaging() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertFalse("'Previous' button should be disabled", page.isPreviousBtnEnabled());
		assertTrue("'Next' button should be enabled", page.isNextBtnEnabled());
		assertEquals("Should be 3 pages of samples", 3, page.getPaginationCount());
	}

	@Test
	public void testAssociatedProjects() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("Should be displaying 23 samples", "Showing 1 to 10 of 23 entries", page.getTableInfo());
		page.displayAssociatedProject();
		assertEquals("Should be displaying 24 samples", "Showing 1 to 10 of 24 entries", page.getTableInfo());
	}

	@Test
	public void testSampleSelection() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("Should be 0 selected samples", "No samples selected", page.getSelectedInfoText());

		page.selectSample(0);
		assertEquals("Should be 1 selected samples", "1 sample selected", page.getSelectedInfoText());

		page.selectSampleWithShift(4);
		assertEquals("Should be 5 selected samples", "5 samples selected", page.getSelectedInfoText());

		page.selectAllSamples();
		assertEquals("Should have all samples selected", "23 samples selected", page.getSelectedInfoText());

		page.deselectAllSamples();
		assertEquals("Should be 0 selected samples", "No samples selected", page.getSelectedInfoText());
	}

	@Test
	public void testAddSamplesToCart() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.selectSample(0);
		page.selectSampleWithShift(4);
		assertEquals("Should be 5 selected samples", "5 samples selected", page.getSelectedInfoText());


		page.goToNextPage();
		page.selectSample(1);
		page.selectSample(2);
		assertEquals("Should be 7 selected samples", "7 samples selected", page.getSelectedInfoText());

		page.addSelectedSamplesToCart();
		assertEquals("Should be 7 samples in the cart", 7, page.getCartCount());
		page.selectPaginationPage(1);

		// Need to make sure select all samples works
		page.selectAllSamples();
		page.addSelectedSamplesToCart();
		assertEquals("Should be 23 samples in the cart", 23, page.getCartCount());
	}

	@Test
	public void testMergeSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		// Select some samples
		page.selectSample(0);
		page.selectSample(2);
		assertEquals("Should be 2 selected samples", "2 samples selected", page.getSelectedInfoText());

		// Merge these samples with the original name
		List<String> originalNames = Lists.newArrayList(page.getSampleNamesOnPage().get(0),
				page.getSampleNamesOnPage().get(2));
		page.mergeSamplesWithOriginalName();
		List<String> mergeNames = Lists.newArrayList(page.getSampleNamesOnPage().get(0),
				page.getSampleNamesOnPage().get(2));
		assertEquals("Should still the first samples name", originalNames.get(0), mergeNames.get(0));
		assertFalse("Should have different sample second since it was merged",
				originalNames.get(1).equals(mergeNames.get(1)));

		// Merge with a new name
		page.selectSample(0);
		page.selectSample(1);
		String newSampleName = "NEW_NAME";
		page.mergeSamplesWithNewName(newSampleName);
		String name = page.getSampleNamesOnPage().get(0);
		assertEquals("Should have the new sample name", newSampleName, name);
	}

	@Test
	public void testRemoteSampleManagerButtonDisabled() {
		LoginPage.loginAsManager(driver());

		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 7);
		page.selectSample(0);
		page.selectSample(1);

		page.waitUntilShareButtonVisible();
		assertTrue("Share button should be enabled", page.isShareBtnEnabled());
		assertFalse("Merge button should not be enabled", page.isMergeBtnEnabled());
	}

	@Test
	public void testRemoveSamplesFromProject() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Select some samples
		page.selectSample(0);
		page.selectSample(1);

		// Remove process
		page.removeSamples();
		assertEquals("Should be only 3 pages of projects now", 3, page.getPaginationCount());
		page.selectPaginationPage(2);
		assertEquals("Should only be displaying 10 samples.", 10, page.getNumberProjectsDisplayed());
		assertEquals("Should be 0 selected samples", "No samples selected", page.getSelectedInfoText());
	}

	@Test
	public void testFilteringSamplesByProperties() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("Should have 23 projects displayed", "Showing 1 to 10 of 23 entries", page.getTableInfo());
		page.filterByName("5");
		assertEquals("Should have 19 projects displayed", "Showing 1 to 10 of 19 entries", page.getTableInfo());
		page.filterByName("52");
		assertEquals("Should have 3 projects displayed", "Showing 1 to 3 of 3 entries", page.getTableInfo());

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.selectAllSamples();
		assertEquals("Should only have 3 samples selected", "3 samples selected", page.getSelectedInfoText());

		// Test clearing the filters
		page.clearFilter();
		assertEquals("Should have 23 projects displayed", "Showing 1 to 10 of 23 entries", page.getTableInfo());

		// Should ignore case
		page.filterByName("sample");
		assertEquals("Should ignore case when filtering", "Showing 1 to 10 of 23 entries", page.getTableInfo());

		// Test date range filter
		page.clearFilter();
		assertEquals("Should have 23 samples displayed", "Showing 1 to 10 of 23 entries", page.getTableInfo());

		// Should find sample with underscores not hyphens
		page.filterByName("sample_5_fg_22");
		assertEquals("Should only have returned 2 sample", 2, page.getSampleNamesOnPage().size());
		assertEquals("Should have sample with exact name", "sample_5_fg_22", page.getSampleNamesOnPage().get(0));

		// Should find sample with hyphens not underscores
		page.filterByName("sample-5-fg-22");
		assertEquals("Should only have returned 1 sample", 1, page.getSampleNamesOnPage().size());
		assertEquals("Should have sample with exact name", "sample-5-fg-22", page.getSampleNamesOnPage().get(0));

	}

	@Test
	public void testFilteringWithDates() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.filterByDateRange("07/06/2015 - 07/09/2015");
		assertEquals("Should ignore case when filtering", "Showing 1 to 4 of 4 entries", page.getTableInfo());

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.selectAllSamples();
		assertEquals("Should only have 4 samples selected after filter", "4 samples selected", page.getSelectedInfoText());

		// Test clearing the filters
		page.clearFilter();
		assertEquals("Should have 23 samples displayed", "Showing 1 to 10 of 23 entries", page.getTableInfo());
	}

	@Test
	public void testCartFunctionality() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Select some samples
		page.selectSample(0);
		page.selectSample(1);

		// Add them to the cart
		page.addSelectedSamplesToCart();
		assertEquals("Should be two items in the cart", 2, page.getCartCount());

		page.selectSample(5);
		page.addSelectedSamplesToCart();
		assertEquals("Should be three items in the cart", 3, page.getCartCount());
	}

	@Test
	public void testLinkerFunctionalityForProject() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		page.openLinkerModal();
		assertEquals("Should display the correct linker for entire project", "ngsArchiveLinker.pl -p 1 -t fastq",
				page.getLinkerText());
	}

	@Test
	public void testLinkerFunctionalityForSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		page.filterByDateRange("07/06/2015 - 07/09/2015");
		assertEquals("Should ignore case when filtering", "Showing 1 to 4 of 4 entries", page.getTableInfo());

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.selectAllSamples();
		assertEquals("Should only have 4 samples selected after filter", "4 samples selected", page.getSelectedInfoText());

		// Open the linker modal
		page.openLinkerModal();
		assertEquals("Should display the correct linker command", "ngsArchiveLinker.pl -p 1 -s 9 -s 8 -s 7 -s 6 -t fastq",
				page.getLinkerText());

	}

	@Test
	public void testLinkerFunctionalityForFiletype() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		page.openLinkerModal();
		page.clickLinkerFileType("assembly");
		assertEquals("Should display the correct linker for entire project", "ngsArchiveLinker.pl -p 1 -t fastq,assembly",
				page.getLinkerText());
	}

	@Test
	public void testAddNewSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.openCreateNewSampleModal();
		page.enterSampleName("BAD");
		assertTrue("Should show a warning message", page.isSampleNameErrorDisplayed());
		page.enterSampleName("BAD NAME");
		assertTrue("Should show a warning message", page.isSampleNameErrorDisplayed());
		page.enterSampleName("BAD ***");
		assertTrue("Should show a warning message", page.isSampleNameErrorDisplayed());
		page.enterSampleName("GOOD_NAME");
		assertFalse("Sample name error should not be displayed", page.isSampleNameErrorDisplayed());
	}
}