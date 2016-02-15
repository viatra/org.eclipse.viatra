package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import system.queries.JobInfoCorrespondenceMatch;
import system.queries.JobInfoCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate JobInfoCorrespondenceMatcher in a type-safe way.
 * 
 * @see JobInfoCorrespondenceMatcher
 * @see JobInfoCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class JobInfoCorrespondenceQuerySpecification extends BaseGeneratedEMFQuerySpecification<JobInfoCorrespondenceMatcher> {
  private JobInfoCorrespondenceQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static JobInfoCorrespondenceQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected JobInfoCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return JobInfoCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public JobInfoCorrespondenceMatch newEmptyMatch() {
    return JobInfoCorrespondenceMatch.newEmptyMatch();
  }
  
  @Override
  public JobInfoCorrespondenceMatch newMatch(final Object... parameters) {
    return JobInfoCorrespondenceMatch.newMatch((system.Job) parameters[0], (operation.RuntimeInformation) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static JobInfoCorrespondenceQuerySpecification INSTANCE = make();
    
    public static JobInfoCorrespondenceQuerySpecification make() {
      return new JobInfoCorrespondenceQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static JobInfoCorrespondenceQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "system.queries.JobInfoCorrespondence";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("Job","Info");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("Job", "system.Job"),new PParameter("Info", "operation.RuntimeInformation"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_Job = body.getOrCreateVariableByName("Job");
      		PVariable var_Info = body.getOrCreateVariableByName("Info");
      		PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      		body.setExportedParameters(Arrays.<ExportedParameter>asList(
      			new ExportedParameter(body, var_Job, "Job"),
      			
      			new ExportedParameter(body, var_Info, "Info")
      		));
      		new TypeConstraint(body, new FlatTuple(var_Job), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://system/1.0", "Job")));
      		new TypeConstraint(body, new FlatTuple(var_Info), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "RuntimeInformation")));
      		new TypeConstraint(body, new FlatTuple(var_CLE), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "ChecklistEntry")));
      		new TypeConstraint(body, new FlatTuple(var_CLE, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "info")));
      		new Equality(body, var__virtual_0_, var_Info);
      		new TypeConstraint(body, new FlatTuple(var_CLE), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "ChecklistEntry")));
      		new TypeConstraint(body, new FlatTuple(var_CLE, var__virtual_1_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "jobs")));
      		new Equality(body, var__virtual_1_, var_Job);
      		bodies.add(body);
      	}
      	{
      		PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      		annotation.addAttribute("feature", "info");
      		addAnnotation(annotation);
      	}
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
