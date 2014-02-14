package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.TaskChecklistEntryJobCorrespondenceMatcher;
import operation.queries.util.ChecklistEntryJobCorrespondenceQuerySpecification;
import operation.queries.util.ChecklistEntryTaskCorrespondenceQuerySpecification;
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
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate TaskChecklistEntryJobCorrespondenceMatcher in a type-safe way.
 * 
 * @see TaskChecklistEntryJobCorrespondenceMatcher
 * @see TaskChecklistEntryJobCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class TaskChecklistEntryJobCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<TaskChecklistEntryJobCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TaskChecklistEntryJobCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected TaskChecklistEntryJobCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TaskChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "operation.queries.TaskChecklistEntryJobCorrespondence";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Task","CLE","Job");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Task", "process.Task"),new PParameter("CLE", "operation.ChecklistEntry"),new PParameter("Job", "system.Job"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() {
    return bodies;
  }
  
  private TaskChecklistEntryJobCorrespondenceQuerySpecification() throws IncQueryException {
    super();
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    {
      PBody body = new PBody(this);
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      PVariable var_Job = body.getOrCreateVariableByName("Job");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_Task, "Task"), 
        new ExportedParameter(body, var_CLE, "CLE"), 
        new ExportedParameter(body, var_Job, "Job")
      ));
      
      
      
      new PositivePatternCall(body, new FlatTuple(var_CLE, var_Task), ChecklistEntryTaskCorrespondenceQuerySpecification.instance());
      new PositivePatternCall(body, new FlatTuple(var_CLE, var_Job), ChecklistEntryJobCorrespondenceQuerySpecification.instance());
      bodies.add(body);
    }
    {
      PAnnotation annotation = new PAnnotation("Constraint");
      annotation.addAttribute("message","Task $Task.name$ connected to Job $Job.name$ through entry $CLE.name$");
      annotation.addAttribute("location",new ParameterReference("CLE"));
      annotation.addAttribute("severity","warning");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
  }
  
  private Set<PBody> bodies = Sets.newHashSet();;
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<TaskChecklistEntryJobCorrespondenceQuerySpecification> {
    @Override
    public TaskChecklistEntryJobCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static TaskChecklistEntryJobCorrespondenceQuerySpecification INSTANCE = make();
    
    public static TaskChecklistEntryJobCorrespondenceQuerySpecification make() {
      try {
      	return new TaskChecklistEntryJobCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
