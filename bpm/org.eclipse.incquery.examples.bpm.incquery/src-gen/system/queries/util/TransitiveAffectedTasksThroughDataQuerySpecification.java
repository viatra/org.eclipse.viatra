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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import system.queries.TransitiveAffectedTasksThroughDataMatch;
import system.queries.TransitiveAffectedTasksThroughDataMatcher;
import system.queries.util.TasksAffectedThroughDataQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate TransitiveAffectedTasksThroughDataMatcher in a type-safe way.
 * 
 * @see TransitiveAffectedTasksThroughDataMatcher
 * @see TransitiveAffectedTasksThroughDataMatch
 * 
 */
@SuppressWarnings("all")
public final class TransitiveAffectedTasksThroughDataQuerySpecification extends BaseGeneratedEMFQuerySpecification<TransitiveAffectedTasksThroughDataMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TransitiveAffectedTasksThroughDataQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
  }
  
  @Override
  protected TransitiveAffectedTasksThroughDataMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TransitiveAffectedTasksThroughDataMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "system.queries.TransitiveAffectedTasksThroughData";
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
  public TransitiveAffectedTasksThroughDataMatch newEmptyMatch() {
    return TransitiveAffectedTasksThroughDataMatch.newEmptyMatch();
  }
  
  @Override
  public TransitiveAffectedTasksThroughDataMatch newMatch(final Object... parameters) {
    return TransitiveAffectedTasksThroughDataMatch.newMatch((process.Task) parameters[0], (process.Task) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody>  bodies = Sets.newLinkedHashSet();
    
    {
    	PBody body = new PBody(this);
    	PVariable var_SourceTask = body.getOrCreateVariableByName("SourceTask");
    	PVariable var_AffectedTask = body.getOrCreateVariableByName("AffectedTask");
    	body.setExportedParameters(Arrays.<ExportedParameter>asList(
    		new ExportedParameter(body, var_SourceTask, "SourceTask"),
    		
    		new ExportedParameter(body, var_AffectedTask, "AffectedTask")
    	));
    new TypeUnary(body, var_SourceTask, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
    new TypeUnary(body, var_AffectedTask, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
    	new BinaryTransitiveClosure(body, new FlatTuple(var_SourceTask, var_AffectedTask), TasksAffectedThroughDataQuerySpecification.instance());
    	bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static TransitiveAffectedTasksThroughDataQuerySpecification INSTANCE = make();
    
    public static TransitiveAffectedTasksThroughDataQuerySpecification make() {
      return new TransitiveAffectedTasksThroughDataQuerySpecification();					
    }
  }
}
