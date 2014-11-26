package system.queries;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.incquery.validation.runtime.Constraint;
import org.eclipse.incquery.validation.runtime.ValidationUtil;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import system.queries.UndefinedServiceTasksMatch;
import system.queries.util.UndefinedServiceTasksQuerySpecification;
import system.queries.UndefinedServiceTasksMatcher;

public class UndefinedServiceTasksConstraint0 extends Constraint<UndefinedServiceTasksMatch> {

	private UndefinedServiceTasksQuerySpecification querySpecification;

	public UndefinedServiceTasksConstraint0() throws IncQueryException {
		querySpecification = UndefinedServiceTasksQuerySpecification.instance();
	}

	@Override
	public String getMessage() {
		return "Service Task $Task.name$ has no job";
	}

	@Override
	public EObject getLocationObject(UndefinedServiceTasksMatch signature) {
		Object location = signature.get("Task");
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
	public BaseGeneratedEMFQuerySpecification<UndefinedServiceTasksMatcher> getQuerySpecification() {
		return querySpecification;
	}
}
