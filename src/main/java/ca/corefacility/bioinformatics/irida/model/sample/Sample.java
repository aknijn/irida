package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.validators.annotations.Latitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.Longitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmission;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmissionOneOf;
import ca.corefacility.bioinformatics.irida.web.controller.api.json.DateJson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A biological sample. Each sample may correspond to many files.
 * A {@link Sample} comprises of many attributes. The attributes assigned to a
 * {@link Sample} correspond to the NCBI Pathogen BioSample attributes. See
 * <a href=
 * "https://submit.ncbi.nlm.nih.gov/biosample/template/?package=Pathogen.cl.1.0&action=definition"
 * >BioSample Attributes: Package Pathogen</a> for more information.
 */
@Entity
@NamedEntityGraph(name = "sampleOnly")
@Table(name = "sample")
@Audited
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample extends IridaRepresentationModel
		implements MutableIridaThing, Comparable<Sample>, RemoteSynchronizable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@NotNull(message = "{sample.name.notnull}")
	@Size(min = 3, message = "{sample.name.too.short}")
	@ValidSampleName
	private String sampleName;

	@Lob
	private String description;

	/**
	 * The most descriptive organism name for this sample (to the species, if
	 * relevant).
	 */
	@NotNull(message = "{sample.organism.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.organism.too.short}")
	private String organism;

	/**
	 * identification or description of the specific individual from which this
	 * sample was obtained
	 * ISS patient condition
	 */
	@NotNull(message = "{sample.isolate.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.isolate.too.short}")
	private String isolate;

	/**
	 * microbial or eukaryotic strain name
	 */
	@NotNull(message = "{sample.strain.name.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.strain.name.too.short}")
	private String strain;

	/**
	 * Name of the person who collected the sample.
	 */
	@NotNull(message = "{sample.collected.by.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.collected.by.too.short}")
	private String collectedBy;

	/**
	 * Date of sampling
	 */
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = DateJson.DateSerializer.class)
	@JsonDeserialize(using = DateJson.DateDeserializer.class)
	@NotNull(message = "{sample.collection.date.notnull}", groups = NCBISubmission.class)
	@Schema(type = "string", format = "date")
	private Date collectionDate;

	/**
	 * ISS Date of arrival of the sample
	 */
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = DateJson.DateSerializer.class)
	@JsonDeserialize(using = DateJson.DateDeserializer.class)
	@Schema(type = "string", format = "date")
	private Date arrivalDate;

	/**
	 * ISS Name of the person who sequenced the sample.
	 */
	@Size(min = 3, message = "{sample.sequenced.by.too.short}")
	private String sequencedBy;

	/**
	 * Geographical origin of the sample (country derived from
	 * http://www.insdc.org/documents/country-qualifier-vocabulary).
	 * ISS Regions exist with spaces, hyphens and apostrophes
	 */
	@NotNull(message = "{sample.geographic.location.name.notnull}", groups = NCBISubmission.class)
	@Pattern(regexp = "[a-zA-Z.' \\-]+(:\\w+(:\\w+)?)?", message = "{sample.geographic.location.name.pattern}")
	@Size(min = 3, message = "{sample.geographic.location.name.too.short}")
	private String geographicLocationName;

	@Size(min = 3, message = "{sample.geographic2.location.name.too.short}")
	private String geographicLocationName2;
	@Size(min = 3, message = "{sample.geographic3.location.name.too.short}")
	private String geographicLocationName3;

	/**
	 * Describes the physical, environmental and/or local geographical source of
	 * the biological sample from which the sample was derived.
	 */
	@Lob
	@NotNull(message = "{sample.isolation.source.notnull}", groups = NCBISubmission.class)
	private String isolationSource;

	/**
	 * Age range of the patient.
	 */
	private String patientAge;

	/**
	 * Number of Vaccinations of the patient.
	 */
	@Pattern(regexp = "^[0-9]$|^-$", message = "{sample.patient.vaccination.number.pattern}")
	@Size(max = 1, message = "{sample.patient.vaccination.number.too.long}")
	private String patientVaccinationNumber;

	/**
	 * Last Vaccination Date of the patient.
	 */
	@Temporal(TemporalType.DATE)
	@JsonSerialize(as=java.sql.Date.class, using = DateJson.DateSerializer.class)
	@JsonDeserialize(as=java.sql.Date.class, using = DateJson.DateDeserializer.class)
	private Date patientVaccinationDate;

	/**
	 * lat_lon is marked as a *mandatory* attribute in NCBI BioSample, but in
	 * practice many of the fields are shown as "missing".
	 */
	@NotNull(message = "{sample.latitude.notnull}", groups = NCBISubmission.class)
	@Latitude
	private String latitude;

	@NotNull(message = "{sample.longitude.notnull}", groups = NCBISubmission.class)
	@Longitude
	private String longitude;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<ProjectSampleJoin> projects;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<SampleSequencingObjectJoin> sequenceFiles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	@NotAudited
	private List<SampleAddedProjectEvent> events;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "remote_status")
	private RemoteStatus remoteStatus;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private Set<MetadataEntry> metadataEntries;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<SampleGenomeAssemblyJoin> genomeAssemblies;

	public Sample() {
		createdDate = new Date();
	}

	/**
	 * Create a new {@link Sample} with the given name
	 *
	 * @param sampleName The name of the sample
	 */
	public Sample(String sampleName) {
		this();
		this.sampleName = sampleName;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Sample) {
			Sample sample = (Sample) other;
			return Objects.equals(id, sample.id) && Objects.equals(createdDate, sample.createdDate)
					&& Objects.equals(modifiedDate, sample.modifiedDate)
					&& Objects.equals(sampleName, sample.sampleName) && Objects.equals(description, sample.description)
					&& Objects.equals(organism, sample.organism) && Objects.equals(isolate, sample.isolate)
					&& Objects.equals(strain, sample.strain) && Objects.equals(collectedBy, sample.collectedBy)
					&& Objects.equals(collectionDate, sample.collectionDate) 
					&& Objects.equals(arrivalDate, sample.arrivalDate) 
					&& Objects.equals(sequencedBy, sample.sequencedBy)
					&& Objects.equals(geographicLocationName, sample.geographicLocationName)
					&& Objects.equals(geographicLocationName2, sample.geographicLocationName2)
					&& Objects.equals(geographicLocationName3, sample.geographicLocationName3)
					&& Objects.equals(isolationSource, sample.isolationSource)
					&& Objects.equals(patientAge, sample.patientAge)
					&& Objects.equals(patientVaccinationNumber, sample.patientVaccinationNumber)
					&& Objects.equals(patientVaccinationDate, sample.patientVaccinationDate)
					&& Objects.equals(latitude, sample.latitude) && Objects.equals(longitude, sample.longitude);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, createdDate, modifiedDate, sampleName, description, organism, isolate, strain,
				collectedBy, collectionDate, arrivalDate, geographicLocationName, isolationSource, patientAge, 
				patientVaccinationNumber, patientVaccinationDate, latitude, longitude);
	}

	@Override
	public int compareTo(Sample other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	@Override
	public String toString() {
		// @formatter:off
		return "Sample{" + "id=" + id +
				", sampleName='" + sampleName + '\'' +
				", description='" + description + '\'' +
				", collectedBy='" + collectedBy + '\'' +
				", organism='" + organism + '\'' +
				", collectionDate=" + collectionDate +
				", arrivalDate=" + arrivalDate +
				", modifiedDate=" + modifiedDate +
				", createdDate=" + createdDate +
				'}';
		// @formatter:on
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getLabel() {
		return sampleName;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getSequencedBy() {
		return sequencedBy;
	}

	public void setSequencedBy(String sequencedBy) {
		this.sequencedBy = sequencedBy;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getIsolate() {
		return isolate;
	}

	public void setIsolate(String isolate) {
		this.isolate = isolate;
	}

	public String getIsolateValue(int indx) {
		String isolateValue;
		if (isolate == null) {
			isolateValue = "000000";
		} else {
			isolateValue = isolate.substring(indx - 1, indx);
		}
		if (typeof(isolateValue) == "undefined") { isolateValue = 0; }
		return isolateValue;
	}

	public void setIsolateValue(int indx, String isolateValue) {
		String newIsolateValue;
		String newIsolate;
		if (isolate == null) {
			newIsolate = "000000";
		} else {
			newIsolate = this.isolate;
		}
		switch (isolateValue) {
			case "1":
			case "2":
				newIsolateValue = isolateValue;
				break;
			case "Sì":
			case "Si":
				newIsolateValue = "1";
				break;
			case "No":
				newIsolateValue = "2";
				break;
			default:
				newIsolateValue = "0";
		}
		this.isolate = newIsolate.substring(0, indx - 1) + newIsolateValue + newIsolate.substring(indx);
	}

	public String getIsolateText(int indx) {
		String isolateText;
		String isolateValue;
		isolateValue = getIsolateValue(indx);
		
		switch (isolateValue) {
			case "1":
				isolateText = "Sì";
				break;
			case "2":
				isolateText = "No";
				break;
			default:
				isolateText = "ND";
		}
		return isolateText;
	}

	public String getGeographicLocationName() {
		return geographicLocationName;
	}

	public void setGeographicLocationName(String geographicLocationName) {
		this.geographicLocationName = geographicLocationName;
	}

	public String getGeographicLocationName2() {
		return geographicLocationName2;
	}

	public void setGeographicLocationName2(String geographicLocationName2) {
		this.geographicLocationName2 = geographicLocationName2;
	}

	public String getGeographicLocationName3() {
		return geographicLocationName3;
	}

	public void setGeographicLocationName3(String geographicLocationName3) {
		this.geographicLocationName3 = geographicLocationName3;
	}

	public String getIsolationSource() {
		return isolationSource;
	}

	public void setIsolationSource(String isolationSource) {
		this.isolationSource = isolationSource;
	}

	public String getPatientAge() {
		return patientAge;
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	public String getPatientVaccinationNumber() {
		return patientVaccinationNumber;
	}

	public void setPatientVaccinationNumber(String patientVaccinationNumber) {
		this.patientVaccinationNumber = patientVaccinationNumber;
	}

	public Date getPatientVaccinationDate() {
		return patientVaccinationDate;
	}

	public void setPatientVaccinationDate(Date patientVaccinationDate) {
		this.patientVaccinationDate = patientVaccinationDate;
	}

	@Override
	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}

	@Override
	public void setRemoteStatus(RemoteStatus status) {
		this.remoteStatus = status;
	}
}
