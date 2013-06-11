package operation.queries;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.incquery.validation.runtime.Constraint;
import org.eclipse.incquery.validation.runtime.ValidationUtil;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import operation.queries.DataReadByChecklistEntryMatch;
import operation.queries.util.DataReadByChecklistEntryQuerySpecification;
import operation.queries.DataReadByChecklistEntryMatcher;

public class DataReadByChecklistEntryConstraint0 extends Constraint<DataReadByChecklistEntryMatch> {

	private DataReadByChecklistEntryQuerySpecification querySpecification;

	public DataReadByChecklistEntryConstraint0() throws IncQueryException {
		querySpecification = DataReadByChecklistEntryQuerySpecification.instance();
	}

	@Override
	public String getMessage() {
		return "Entry $CLE.name$ connected to $Data.name$ through $Task.name$";
	}

	@Override
	public EObject getLocationObject(DataReadByChecklistEntryMatch signature) {
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
	public BaseGeneratedQuerySpecification<DataReadByChecklistEntryMatcher> getQuerySpecification() {
		return querySpecification;
	}
}
