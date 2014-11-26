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
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import system.queries.UndefinedServiceTasksMatch;
import system.queries.UndefinedServiceTasksMatcher;
import system.queries.util.TaskHasJobQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate UndefinedServiceTasksMatcher in a type-safe way.
 * 
 * @see UndefinedServiceTasksMatcher
 * @see UndefinedServiceTasksMatch
 * 
 */
@SuppressWarnings("all")
public final class UndefinedServiceTasksQuerySpecification extends BaseGeneratedEMFQuerySpecification<UndefinedServiceTasksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static UndefinedServiceTasksQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
  }
  
  @Override
  protected UndefinedServiceTasksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return UndefinedServiceTasksMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "system.queries.UndefinedServiceTasks";
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
  public UndefinedServiceTasksMatch newEmptyMatch() {
    return UndefinedServiceTasksMatch.newEmptyMatch();
  }
  
  @Override
  public UndefinedServiceTasksMatch newMatch(final Object... parameters) {
    return UndefinedServiceTasksMatch.newMatch((process.Task) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody>  bodies = Sets.newLinkedHashSet();
    
    {
    	PBody body = new PBody(this);
    	PVariable var_Task = body.getOrCreateVariableByName("Task");
    	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
    	body.setExportedParameters(Arrays.<ExportedParameter>asList(
    		new ExportedParameter(body, var_Task, "Task")
    	));
    	new ConstantValue(body, var__virtual_0_, getEnumLiteral("http://process/1.0", "TaskKind", "service").getInstance());
    new TypeBinary(body, CONTEXT, var_Task, var__virtual_0_, getFeatureLiteral("http://process/1.0", "Task", "kind"), "http://process/1.0/Task.kind");
    	new NegativePatternCall(body, new FlatTuple(var_Task), TaskHasJobQuerySpecification.instance());
    	bodies.add(body);
    }
    {
    	PAnnotation annotation = new PAnnotation("Constraint");
    	annotation.addAttribute("severity", "warning");
    	annotation.addAttribute("location", new ParameterReference("Task"));
    	annotation.addAttribute("message", "Service Task $Task.name$ has no job");
    	addAnnotation(annotation);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static UndefinedServiceTasksQuerySpecification INSTANCE = make();
    
    public static UndefinedServiceTasksQuerySpecification make() {
      return new UndefinedServiceTasksQuerySpecification();					
    }
  }
}
