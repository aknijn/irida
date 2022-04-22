package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Used as a response for encapsulating a newick string
 * and an optional server message.
 */

public class AnalysisTreeResponse {
	//Tree
	private String newick;
	//Branch
	private String branch;
	//Server message
	private String message;

	public AnalysisTreeResponse() {
	}

	public AnalysisTreeResponse(String newick, String branch, String message) {
		this.newick=newick;
		this.branch=branch;
		this.message=message;
	}

	public String getNewick() {
		return newick;
	}

	public void setNewick(String newick) {
		this.newick = newick;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}