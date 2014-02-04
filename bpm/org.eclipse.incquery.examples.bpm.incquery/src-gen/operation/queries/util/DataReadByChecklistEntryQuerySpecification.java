package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.DataReadByChecklistEntryMatcher;
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
import system.queries.util.DataTaskReadCorrespondenceQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate DataReadByChecklistEntryMatcher in a type-safe way.
 * 
 * @see DataReadByChecklistEntryMatcher
 * @see DataReadByChecklistEntryMatch
 * 
 */
@SuppressWarnings("all")
public final class DataReadByChecklistEntryQuerySpecification extends BaseGeneratedQuerySpecification<DataReadByChecklistEntryMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static DataReadByChecklistEntryQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected DataReadByChecklistEntryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return DataReadByChecklistEntryMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "operation.queries.DataReadByChecklistEntry";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("CLE","Task","Data");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("CLE", "operation.ChecklistEntry"),new PParameter("Task", "process.Task"),new PParameter("Data", "system.Data"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() {
    return bodies;
  }
  
  private DataReadByChecklistEntryQuerySpecification() throws IncQueryException {
    super();
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    {
      PBody body = new PBody(this);
      PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      PVariable var_Data = body.getOrCreateVariableByName("Data");
      new ExportedParameter(body, var_CLE, "CLE");
      new ExportedParameter(body, var_Task, "Task");
      new ExportedParameter(body, var_Data, "Data");
      new PositivePatternCall(body, new FlatTuple(var_CLE, var_Task), ChecklistEntryTaskCorrespondenceQuerySpecification.instance());
      new PositivePatternCall(body, new FlatTuple(var_Data, var_Task), DataTaskReadCorrespondenceQuerySpecification.instance());
      body.setSymbolicParameters(Arrays.asList(var_CLE, var_Task, var_Data));
      bodies.add(body);
    }
    {
      PAnnotation annotation = new PAnnotation("Constraint");
      annotation.addAttribute("message","Entry $CLE.name$ connected to $Data.name$ through $Task.name$");
      annotation.addAttribute("location",new ParameterReference("CLE"));
      annotation.addAttribute("severity","warning");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
  }
  
  private Set<PBody> bodies = Sets.newHashSet();;
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<DataReadByChecklistEntryQuerySpecification> {
    @Override
    public DataReadByChecklistEntryQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static DataReadByChecklistEntryQuerySpecification INSTANCE = make();
    
    public static DataReadByChecklistEntryQuerySpecification make() {
      try {
      	return new DataReadByChecklistEntryQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
