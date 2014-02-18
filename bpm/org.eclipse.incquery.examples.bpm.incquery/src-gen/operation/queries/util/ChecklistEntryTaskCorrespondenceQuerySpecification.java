package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.ChecklistEntryTaskCorrespondenceMatcher;
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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;

/**
 * A pattern-specific query specification that can instantiate ChecklistEntryTaskCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistEntryTaskCorrespondenceMatcher
 * @see ChecklistEntryTaskCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistEntryTaskCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<ChecklistEntryTaskCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistEntryTaskCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ChecklistEntryTaskCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryTaskCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "operation.queries.ChecklistEntryTaskCorrespondence";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("CLE","Task");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("CLE", "operation.ChecklistEntry"),new PParameter("Task", "process.Task"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      PVariable var_TaskId = body.getOrCreateVariableByName("TaskId");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_CLE, "CLE"), 
        new ExportedParameter(body, var_Task, "Task")
      ));
      
      
      new TypeUnary(body, var_Task, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      new TypeBinary(body, context, var_Task, var_TaskId, getFeatureLiteral("http://process/1.0", "ProcessElement", "id"), "http://process/1.0/ProcessElement.id");
      new TypeBinary(body, context, var_CLE, var_TaskId, getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "taskId"), "http://operation/1.0/ChecklistEntry.taskId");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      annotation.addAttribute("feature","task");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private ChecklistEntryTaskCorrespondenceQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ChecklistEntryTaskCorrespondenceQuerySpecification> {
    @Override
    public ChecklistEntryTaskCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ChecklistEntryTaskCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistEntryTaskCorrespondenceQuerySpecification make() {
      try {
      	return new ChecklistEntryTaskCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
