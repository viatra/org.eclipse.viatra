package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.IncorrectEntryInChecklistMatcher;
import operation.queries.util.ChecklistEntryTaskCorrespondenceQuerySpecification;
import operation.queries.util.ChecklistProcessCorrespondenceQuerySpecification;
import operation.queries.util.TaskInProcessQuerySpecification;
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
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate IncorrectEntryInChecklistMatcher in a type-safe way.
 * 
 * @see IncorrectEntryInChecklistMatcher
 * @see IncorrectEntryInChecklistMatch
 * 
 */
@SuppressWarnings("all")
public final class IncorrectEntryInChecklistQuerySpecification extends BaseGeneratedQuerySpecification<IncorrectEntryInChecklistMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IncorrectEntryInChecklistQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected IncorrectEntryInChecklistMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IncorrectEntryInChecklistMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "operation.queries.IncorrectEntryInChecklist";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("ChecklistEntry","Task","Process");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("ChecklistEntry", "operation.ChecklistEntry"),new PParameter("Task", "process.Task"),new PParameter("Process", "process.Process"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_ChecklistEntry = body.getOrCreateVariableByName("ChecklistEntry");
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      PVariable var_Process = body.getOrCreateVariableByName("Process");
      PVariable var_Checklist = body.getOrCreateVariableByName("Checklist");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_ChecklistEntry, "ChecklistEntry"), 
        new ExportedParameter(body, var_Task, "Task"), 
        new ExportedParameter(body, var_Process, "Process")
      ));
      
      
      
      new TypeBinary(body, context, var_Checklist, var_ChecklistEntry, getFeatureLiteral("http://operation/1.0", "Checklist", "entries"), "http://operation/1.0/Checklist.entries");
      new PositivePatternCall(body, new FlatTuple(var_Checklist, var_Process), ChecklistProcessCorrespondenceQuerySpecification.instance());
      new PositivePatternCall(body, new FlatTuple(var_ChecklistEntry, var_Task), ChecklistEntryTaskCorrespondenceQuerySpecification.instance());
      new NegativePatternCall(body, new FlatTuple(var_Task, var_Process), TaskInProcessQuerySpecification.instance().instance());
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("Constraint");
      annotation.addAttribute("message","Entry $ChecklistEntry.name$ corresponds to Task $Task.name$ outside of process $Process.name$ defined for the checklist!");
      annotation.addAttribute("location",new ParameterReference("ChecklistEntry"));
      annotation.addAttribute("severity","error");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private IncorrectEntryInChecklistQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<IncorrectEntryInChecklistQuerySpecification> {
    @Override
    public IncorrectEntryInChecklistQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static IncorrectEntryInChecklistQuerySpecification INSTANCE = make();
    
    public static IncorrectEntryInChecklistQuerySpecification make() {
      try {
      	return new IncorrectEntryInChecklistQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
