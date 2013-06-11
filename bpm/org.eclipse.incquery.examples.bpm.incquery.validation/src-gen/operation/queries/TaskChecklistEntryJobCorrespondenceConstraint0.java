package operation.queries;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.incquery.validation.runtime.Constraint;
import org.eclipse.incquery.validation.runtime.ValidationUtil;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import operation.queries.TaskChecklistEntryJobCorrespondenceMatch;
import operation.queries.util.TaskChecklistEntryJobCorrespondenceQuerySpecification;
import operation.queries.TaskChecklistEntryJobCorrespondenceMatcher;

public class TaskChecklistEntryJobCorrespondenceConstraint0 extends Constraint<TaskChecklistEntryJobCorrespondenceMatch> {

	private TaskChecklistEntryJobCorrespondenceQuerySpecification querySpecification;

	public TaskChecklistEntryJobCorrespondenceConstraint0() throws IncQueryException {
		querySpecification = TaskChecklistEntryJobCorrespondenceQuerySpecification.instance();
	}

	@Override
	public String getMessage() {
		return "Task $Task.name$ connected to Job $Job.name$ through entry $CLE.name$";
	}

	@Override
	public EObject getLocationObject(TaskChecklistEntryJobCorrespondenceMatch signature) {
		Object location = signature.get("CLE");
		if(location instanceof EObject){
			return (EObject) location;
		}
		return null;
	}
	
	@Override
	public int getSeverity() {
		return ValidationUtil.getSeverity("warning");
	}
	
	@Override
	public BaseGeneratedQuerySpecification<TaskChecklistEntryJobCorrespondenceMatcher> getQuerySpecification() {
		return querySpecification;
	}
}
