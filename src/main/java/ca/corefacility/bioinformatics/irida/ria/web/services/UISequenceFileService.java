package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

@Component
public class UISequenceFileService {
	private final SequencingObjectService sequencingObjectService;

	@Autowired
	public UISequenceFileService(SequencingObjectService sequencingObjectService) {
		this.sequencingObjectService = sequencingObjectService;
	}

	private void getSingleEndFilesForSample(Sample sample) {
		Collection<SampleSequencingObjectJoin> joins = sequencingObjectService.getSequencesForSampleOfType(sample,
				SingleEndSequenceFile.class);
	}

	public List<SequencingObject>  getPairedEndFilesForSample(Sample sample) {
		Collection<SampleSequencingObjectJoin> joins = sequencingObjectService.getSequencesForSampleOfType(sample,
				SequenceFilePair.class);
		return joins.stream()
				.map(SampleSequencingObjectJoin::getObject)
				.collect(Collectors.toList());
	}
}
