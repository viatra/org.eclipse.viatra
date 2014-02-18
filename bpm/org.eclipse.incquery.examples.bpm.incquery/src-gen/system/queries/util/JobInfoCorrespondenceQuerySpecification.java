package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import system.queries.JobInfoCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate JobInfoCorrespondenceMatcher in a type-safe way.
 * 
 * @see JobInfoCorrespondenceMatcher
 * @see JobInfoCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class JobInfoCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<JobInfoCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static JobInfoCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected JobInfoCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return JobInfoCorrespondenceMatcher.on(engine);
  }
  
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
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Job = body.getOrCreateVariableByName("Job");
      PVariable var_Info = body.getOrCreateVariableByName("Info");
      PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_Job, "Job"), 
        new ExportedParameter(body, var_Info, "Info")
      ));
      
      
      new TypeBinary(body, context, var_CLE, var_Info, getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "info"), "http://operation/1.0/ChecklistEntry.info");
      new TypeBinary(body, context, var_CLE, var_Job, getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "jobs"), "http://operation/1.0/ChecklistEntry.jobs");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      annotation.addAttribute("feature","info");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private JobInfoCorrespondenceQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<JobInfoCorrespondenceQuerySpecification> {
    @Override
    public JobInfoCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static JobInfoCorrespondenceQuerySpecification INSTANCE = make();
    
    public static JobInfoCorrespondenceQuerySpecification make() {
      try {
      	return new JobInfoCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
