package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
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
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TasksAffectedThroughDataQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
  }
  
  @Override
  protected TasksAffectedThroughDataMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TasksAffectedThroughDataMatcher.on(engine);
  }
  
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
    return Arrays.asList(new PParameter("SourceTask", "org.eclipse.emf.ecore.EObject"),new PParameter("AffectedTask", "org.eclipse.emf.ecore.EObject"));
  }
  
  @Override
  public TasksAffectedThroughDataMatch newEmptyMatch() {
    return TasksAffectedThroughDataMatch.newEmptyMatch();
  }
  
  @Override
  public TasksAffectedThroughDataMatch newMatch(final Object... parameters) {
    return TasksAffectedThroughDataMatch.newMatch((org.eclipse.emf.ecore.EObject) parameters[0], (org.eclipse.emf.ecore.EObject) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody>  bodies = Sets.newLinkedHashSet();
    
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
    	new PositivePatternCall(body, new FlatTuple(var_Data, var_SourceTask), DataTaskWriteCorrespondenceQuerySpecification.instance());
    	new PositivePatternCall(body, new FlatTuple(var_Data, var_AffectedTask), DataTaskReadCorrespondenceQuerySpecification.instance());
    	bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static TasksAffectedThroughDataQuerySpecification INSTANCE = make();
    
    public static TasksAffectedThroughDataQuerySpecification make() {
      return new TasksAffectedThroughDataQuerySpecification();					
    }
  }
}
