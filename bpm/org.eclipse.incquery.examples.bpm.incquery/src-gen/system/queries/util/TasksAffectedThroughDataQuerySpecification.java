package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
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
import system.queries.TasksAffectedThroughDataMatch;
import system.queries.TasksAffectedThroughDataMatcher;
import system.queries.util.DataTaskReadCorrespondenceQuerySpecification;
import system.queries.util.DataTaskWriteCorrespondenceQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate TasksAffectedThroughDataMatcher in a type-safe way.
 * 
 * @see TasksAffectedThroughDataMatcher
 * @see TasksAffectedThroughDataMatch
 * 
 */
@SuppressWarnings("all")
public final class TasksAffectedThroughDataQuerySpecification extends BaseGeneratedEMFQuerySpecification<TasksAffectedThroughDataMatcher> {
  private TasksAffectedThroughDataQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TasksAffectedThroughDataQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected TasksAffectedThroughDataMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TasksAffectedThroughDataMatcher.on(engine);
  }
  
  @Override
  public TasksAffectedThroughDataMatch newEmptyMatch() {
    return TasksAffectedThroughDataMatch.newEmptyMatch();
  }
  
  @Override
  public TasksAffectedThroughDataMatch newMatch(final Object... parameters) {
    return TasksAffectedThroughDataMatch.newMatch((process.Task) parameters[0], (process.Task) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static TasksAffectedThroughDataQuerySpecification INSTANCE = make();
    
    public static TasksAffectedThroughDataQuerySpecification make() {
      return new TasksAffectedThroughDataQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static TasksAffectedThroughDataQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "system.queries.TasksAffectedThroughData";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("SourceTask","AffectedTask");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("SourceTask", "process.Task"),new PParameter("AffectedTask", "process.Task"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_SourceTask = body.getOrCreateVariableByName("SourceTask");
      	PVariable var_AffectedTask = body.getOrCreateVariableByName("AffectedTask");
      	PVariable var_Data = body.getOrCreateVariableByName("Data");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_SourceTask, "SourceTask"),
      				
      		new ExportedParameter(body, var_AffectedTask, "AffectedTask")
      	));
      	new TypeUnary(body, var_SourceTask, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      	new TypeUnary(body, var_AffectedTask, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      	new TypeUnary(body, var_Data, getClassifierLiteral("http://system/1.0", "Data"), "http://system/1.0/Data");
      	new PositivePatternCall(body, new FlatTuple(var_Data, var_SourceTask), DataTaskWriteCorrespondenceQuerySpecification.instance().getInternalQueryRepresentation());
      	new PositivePatternCall(body, new FlatTuple(var_Data, var_AffectedTask), DataTaskReadCorrespondenceQuerySpecification.instance().getInternalQueryRepresentation());
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
