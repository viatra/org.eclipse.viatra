package system.queries.util;

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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import system.queries.util.JobTaskCorrespondenceQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate TaskHasJobMatcher in a type-safe way.
 * 
 * @see TaskHasJobMatcher
 * @see TaskHasJobMatch
 * 
 */
@SuppressWarnings("all")
final class TaskHasJobQuerySpecification extends BaseGeneratedEMFQuerySpecification<IncQueryMatcher<IPatternMatch>> {
  private TaskHasJobQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TaskHasJobQuerySpecification instance() throws IncQueryException {
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
    private final static TaskHasJobQuerySpecification INSTANCE = make();
    
    public static TaskHasJobQuerySpecification make() {
      return new TaskHasJobQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static TaskHasJobQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "system.queries.TaskHasJob";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("Task");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("Task", "process.Task"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_Task = body.getOrCreateVariableByName("Task");
      	PVariable var__Job = body.getOrCreateVariableByName("_Job");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_Task, "Task")
      	));
      	new TypeUnary(body, var_Task, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      	new PositivePatternCall(body, new FlatTuple(var__Job, var_Task), JobTaskCorrespondenceQuerySpecification.instance().getInternalQueryRepresentation());
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
