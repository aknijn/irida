package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents {@link Sample} metadata in the linelist table.
 */
public class UISampleMetadata extends HashMap<String, String> {
	public static final String PREFIX = StaticMetadataTemplateField.STATIC_FIELD_PREFIX;
	public static final String SAMPLE_NAME = PREFIX + "sample-name";
	public static final String SAMPLE_ID = PREFIX + "sample-id";
	public static final String PROJECT_NAME = PREFIX + "project-name";
	public static final String PROJECT_ID = PREFIX + "project-id";
	public static final String CREATED_DATE = PREFIX + "created";
	public static final String MODIFIED_DATE = PREFIX + "modified";
	public static final String EDITABLE = "editable";
	public static final String OWNER = "owner";

	@Deprecated
	public UISampleMetadata(ProjectSampleJoin join, boolean canModifySample, Set<MetadataEntry> metadata) {
		Project project = join.getSubject();
		Sample sample = join.getObject();

		this.put(SAMPLE_ID, String.valueOf(sample.getId()));
		this.put(SAMPLE_NAME, sample.getLabel());
		this.put(PROJECT_ID, String.valueOf(project.getId()));
		this.put(PROJECT_NAME, project.getLabel());
		this.put(CREATED_DATE, sample.getCreatedDate()
				.toString());
		this.put(MODIFIED_DATE, sample.getModifiedDate()
				.toString());
		this.putAll(getAllMetadataForSample(metadata));
		//TODO: this should be refactored out
		this.put(EDITABLE, String.valueOf(canModifySample));
		this.put(OWNER, String.valueOf(join.isOwner()));
	}

	public UISampleMetadata(Project project, Sample sample, boolean ownership, Set<MetadataEntry> metadata) {

		this.put(SAMPLE_ID, String.valueOf(sample.getId()));
		this.put(SAMPLE_NAME, sample.getLabel());
		this.put(PROJECT_ID, String.valueOf(project.getId()));
		this.put(PROJECT_NAME, project.getLabel());
		this.put(CREATED_DATE, sample.getCreatedDate()
				.toString());
		this.put(MODIFIED_DATE, sample.getModifiedDate()
				.toString());
		this.putAll(getAllMetadataForSample(metadata));

		//TODO: this should be refactored out
		this.put(EDITABLE, String.valueOf(true));

		this.put(OWNER, String.valueOf(ownership));
	}

	/**
	 * Convert the sample metadata into a format that can be consumed by Ag Grid.
	 *
	 * @param metadataEntries the Metadata entries
	 * @return {@link Map} of {@link String} field and {@link String} value
	 */
	private Map<String, String> getAllMetadataForSample(Set<MetadataEntry> metadataEntries) {
		Map<String, String> entries = new HashMap<>();
		for (MetadataEntry entry : metadataEntries) {

			// Label must be converted into the proper format for client side look up purposes in Ag Grid.
			entries.put(entry.getField()
					.getFieldKey(), entry.getValue());
		}
		return entries;
	}

}
