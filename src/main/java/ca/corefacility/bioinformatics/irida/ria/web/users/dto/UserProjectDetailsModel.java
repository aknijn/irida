package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Used to represent user {@link Project}s on the UI user account projects page.
 */
public class UserProjectDetailsModel extends TableModel {
	private Long projectId;
	private String projectName;
	private String roleName;
	private Date createdDate;
	private boolean isManager;
	private boolean isEmailSubscribed;

	public UserProjectDetailsModel(ProjectSubscription projectSubscription, ProjectRole projectRole) {
		super(projectSubscription.getId(), projectSubscription.getProject()
				.getName(), projectSubscription.getCreatedDate(), null);

		this.projectId = projectSubscription.getProject()
				.getId();
		this.projectName = projectSubscription.getProject()
				.getName();
		this.isManager = projectRole.equals(ProjectRole.PROJECT_OWNER);
		this.isEmailSubscribed = projectSubscription.isEmailSubscription();
		this.roleName = projectRole.toString();
		this.createdDate = projectSubscription.getCreatedDate();
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isManager() {
		return isManager;
	}

	public void setManager(boolean manager) {
		isManager = manager;
	}

	public boolean isEmailSubscribed() {
		return isEmailSubscribed;
	}

	public void setEmailSubscribed(boolean emailSubscribed) {
		isEmailSubscribed = emailSubscribed;
	}

}
