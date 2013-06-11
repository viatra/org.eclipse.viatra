package operation.queries;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.incquery.validation.runtime.Constraint;
import org.eclipse.incquery.validation.runtime.ValidationUtil;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import operation.queries.IncorrectEntryInChecklistMatch;
import operation.queries.util.IncorrectEntryInChecklistQuerySpecification;
import operation.queries.IncorrectEntryInChecklistMatcher;

public class IncorrectEntryInChecklistConstraint0 extends Constraint<IncorrectEntryInChecklistMatch> {

	private IncorrectEntryInChecklistQuerySpecification querySpecification;

	public IncorrectEntryInChecklistConstraint0() throws IncQueryException {
		querySpecification = IncorrectEntryInChecklistQuerySpecification.instance();
	}

	@Override
	public String getMessage() {
		return "Entry $ChecklistEntry.name$ corresponds to Task $Task.name$ outside of process $Process.name$ defined for the checklist!";
	}

	@Override
	public EObject getLocationObject(IncorrectEntryInChecklistMatch signature) {
		Object location = signature.get("ChecklistEntry");
		if(location instanceof EObject){
			return (EObject) location;
		}
		return null;
	}
	
	@Override
	public int getSeverity() {
		return ValidationUtil.getSeverity("error");
	}
	
	@Override
	public BaseGeneratedQuerySpecification<IncorrectEntryInChecklistMatcher> getQuerySpecification() {
		return querySpecification;
	}
}
