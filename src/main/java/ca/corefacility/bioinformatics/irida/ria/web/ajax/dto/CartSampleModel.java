package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Used to represent a {@link Sample} on the UI Cart Page.
 * Keeping this as simple as possible as there could be a lot of these asked for.
 */
public class CartSampleModel {
	private final Long id;
	private final String label;
	private final List<SequencingObject> pairs;

	public CartSampleModel(Sample sample, List<SequencingObject> pairedData) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.pairs = pairedData;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<SequencingObject> getPairs() {
		return pairs;
	}
}
