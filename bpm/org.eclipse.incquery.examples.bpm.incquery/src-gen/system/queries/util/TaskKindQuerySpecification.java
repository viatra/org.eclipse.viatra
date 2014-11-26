package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;

/**
 * A pattern-specific query specification that can instantiate TaskKindMatcher in a type-safe way.
 * 
 * @see TaskKindMatcher
 * @see TaskKindMatch
 * 
 */
@SuppressWarnings("all")
final class TaskKindQuerySpecification extends BaseGeneratedEMFQuerySpecification<IncQueryMatcher<IPatternMatch>> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TaskKindQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
  }
  
  @Override
  protected IncQueryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "system.queries.TaskKind";
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Task","Kind");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Task", "process.Task"),new PParameter("Kind", "process.TaskKind"));
  }
  
  @Override
  public IPatternMatch newEmptyMatch() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public IPatternMatch newMatch(final Object... parameters) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody>  bodies = Sets.newLinkedHashSet();
    
    {
    	PBody body = new PBody(this);
    	PVariable var_Task = body.getOrCreateVariableByName("Task");
    	PVariable var_Kind = body.getOrCreateVariableByName("Kind");
    	body.setExportedParameters(Arrays.<ExportedParameter>asList(
    		new ExportedParameter(body, var_Task, "Task"),
    		
    		new ExportedParameter(body, var_Kind, "Kind")
    	));
    new TypeBinary(body, CONTEXT, var_Task, var_Kind, getFeatureLiteral("http://process/1.0", "Task", "kind"), "http://process/1.0/Task.kind");
    	bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static TaskKindQuerySpecification INSTANCE = make();
    
    public static TaskKindQuerySpecification make() {
      return new TaskKindQuerySpecification();					
    }
  }
}
