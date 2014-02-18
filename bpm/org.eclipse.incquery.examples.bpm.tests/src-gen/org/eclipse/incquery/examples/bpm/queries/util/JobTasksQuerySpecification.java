package org.eclipse.incquery.examples.bpm.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.examples.bpm.queries.JobTasksMatcher;
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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;

/**
 * A pattern-specific query specification that can instantiate JobTasksMatcher in a type-safe way.
 * 
 * @see JobTasksMatcher
 * @see JobTasksMatch
 * 
 */
@SuppressWarnings("all")
public final class JobTasksQuerySpecification extends BaseGeneratedQuerySpecification<JobTasksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static JobTasksQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected JobTasksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return JobTasksMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.examples.bpm.queries.jobTasks";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Job","Task");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Job", "system.Job"),new PParameter("Task", "process.Task"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() {
    super();
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    {
      PBody body = new PBody(this);
      PVariable var_Job = body.getOrCreateVariableByName("Job");
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_Job, "Job"), 
        new ExportedParameter(body, var_Task, "Task")
      ));
      
      
      new TypeBinary(body, context, var_Job, var_Task, getFeatureLiteral("http://system/1.0", "Job", "tasks"), "http://system/1.0/Job.tasks");
      bodies.add(body);
    }
    setStatus(PQueryStatus.OK);
  }
  
  private JobTasksQuerySpecification() throws IncQueryException {
    super();setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  private Set<PBody> bodies = Sets.newHashSet();;
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<JobTasksQuerySpecification> {
    @Override
    public JobTasksQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static JobTasksQuerySpecification INSTANCE = make();
    
    public static JobTasksQuerySpecification make() {
      try {
      	return new JobTasksQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
