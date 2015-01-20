package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * A pattern-specific query specification that can instantiate TaskInProcessMatcher in a type-safe way.
 * 
 * @see TaskInProcessMatcher
 * @see TaskInProcessMatch
 * 
 */
@SuppressWarnings("all")
final class TaskInProcessQuerySpecification extends BaseGeneratedEMFQuerySpecification<IncQueryMatcher<IPatternMatch>> {
  private TaskInProcessQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TaskInProcessQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected IncQueryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public IPatternMatch newEmptyMatch() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public IPatternMatch newMatch(final Object... parameters) {
    throw new UnsupportedOperationException();
  }
  
  private static class LazyHolder {
    private final static TaskInProcessQuerySpecification INSTANCE = make();
    
    public static TaskInProcessQuerySpecification make() {
      return new TaskInProcessQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static TaskInProcessQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "operation.queries.TaskInProcess";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("Task","Process");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("Task", "process.Task"),new PParameter("Process", "process.Process"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_Task = body.getOrCreateVariableByName("Task");
      	PVariable var_Process = body.getOrCreateVariableByName("Process");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_Task, "Task"),
      				
      		new ExportedParameter(body, var_Process, "Process")
      	));
      	new TypeUnary(body, var_Task, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      	new TypeBinary(body, CONTEXT, var_Process, var_Task, getFeatureLiteral("http://process/1.0", "Process", "contents"), "http://process/1.0/Process.contents");
      	bodies.add(body);
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
