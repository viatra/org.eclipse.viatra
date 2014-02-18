package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
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
public final class TransitiveAffectedTasksThroughDataQuerySpecification extends BaseGeneratedQuerySpecification<TransitiveAffectedTasksThroughDataMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TransitiveAffectedTasksThroughDataQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
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
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_SourceTask = body.getOrCreateVariableByName("SourceTask");
      PVariable var_AffectedTask = body.getOrCreateVariableByName("AffectedTask");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_SourceTask, "SourceTask"), 
        new ExportedParameter(body, var_AffectedTask, "AffectedTask")
      ));
      
      
      new TypeUnary(body, var_SourceTask, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      new TypeUnary(body, var_AffectedTask, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
      new BinaryTransitiveClosure(body, new FlatTuple(var_SourceTask, var_AffectedTask), TasksAffectedThroughDataQuerySpecification.instance().instance());
      bodies.add(body);
    }setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private TransitiveAffectedTasksThroughDataQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<TransitiveAffectedTasksThroughDataQuerySpecification> {
    @Override
    public TransitiveAffectedTasksThroughDataQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static TransitiveAffectedTasksThroughDataQuerySpecification INSTANCE = make();
    
    public static TransitiveAffectedTasksThroughDataQuerySpecification make() {
      try {
      	return new TransitiveAffectedTasksThroughDataQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
