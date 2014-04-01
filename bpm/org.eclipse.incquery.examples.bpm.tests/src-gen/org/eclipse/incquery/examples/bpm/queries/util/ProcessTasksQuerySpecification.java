package org.eclipse.incquery.examples.bpm.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.examples.bpm.queries.ProcessTasksMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;

/**
 * A pattern-specific query specification that can instantiate ProcessTasksMatcher in a type-safe way.
 * 
 * @see ProcessTasksMatcher
 * @see ProcessTasksMatch
 * 
 */
@SuppressWarnings("all")
public final class ProcessTasksQuerySpecification extends BaseGeneratedQuerySpecification<ProcessTasksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ProcessTasksQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ProcessTasksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ProcessTasksMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.examples.bpm.queries.processTasks";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Proc","Task");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Proc", "process.Process"),new PParameter("Task", "process.Activity"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Proc = body.getOrCreateVariableByName("Proc");
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Proc, "Proc"), 
        new ExportedParameter(body, var_Task, "Task")
      ));
      
      
      new TypeBinary(body, context, var_Proc, var_Task, getFeatureLiteral("http://process/1.0", "Process", "contents"), "http://process/1.0/Process.contents");
      bodies.add(body);
    }setStatus(PQuery.PQueryStatus.OK);
    return bodies;
  }
  
  private ProcessTasksQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQuery.PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ProcessTasksQuerySpecification> {
    @Override
    public ProcessTasksQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ProcessTasksQuerySpecification INSTANCE = make();
    
    public static ProcessTasksQuerySpecification make() {
      try {
      	return new ProcessTasksQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
