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
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
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
public final class UndefinedServiceTasksQuerySpecification extends BaseGeneratedQuerySpecification<UndefinedServiceTasksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static UndefinedServiceTasksQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
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
  public Set<PBody> doGetContainedBodies() {
    return bodies;
  }
  
  private UndefinedServiceTasksQuerySpecification() throws IncQueryException {
    super();
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    {
      PBody body = new PBody(this);
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_Task, "Task")
      ));
      
      new ConstantValue(body, var__virtual_0_, getEnumLiteral("http://process/1.0", "TaskKind", "service").getInstance());
      new TypeBinary(body, context, var_Task, var__virtual_0_, getFeatureLiteral("http://process/1.0", "Task", "kind"), "http://process/1.0/Task.kind");
      new NegativePatternCall(body, new FlatTuple(var_Task), TaskHasJobQuerySpecification.instance());
      bodies.add(body);
    }
    {
      PAnnotation annotation = new PAnnotation("Constraint");
      annotation.addAttribute("message","Service Task $Task.name$ has no job");
      annotation.addAttribute("location",new ParameterReference("Task"));
      annotation.addAttribute("severity","warning");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
  }
  
  private Set<PBody> bodies = Sets.newHashSet();;
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<UndefinedServiceTasksQuerySpecification> {
    @Override
    public UndefinedServiceTasksQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static UndefinedServiceTasksQuerySpecification INSTANCE = make();
    
    public static UndefinedServiceTasksQuerySpecification make() {
      try {
      	return new UndefinedServiceTasksQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
