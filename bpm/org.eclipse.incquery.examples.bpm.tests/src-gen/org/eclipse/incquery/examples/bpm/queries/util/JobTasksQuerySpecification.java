package org.eclipse.incquery.examples.bpm.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.examples.bpm.queries.JobTasksMatch;
import org.eclipse.incquery.examples.bpm.queries.JobTasksMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * A pattern-specific query specification that can instantiate JobTasksMatcher in a type-safe way.
 * 
 * @see JobTasksMatcher
 * @see JobTasksMatch
 * 
 */
@SuppressWarnings("all")
public final class JobTasksQuerySpecification extends BaseGeneratedEMFQuerySpecification<JobTasksMatcher> {
  private JobTasksQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static JobTasksQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected JobTasksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return JobTasksMatcher.on(engine);
  }
  
  @Override
  public JobTasksMatch newEmptyMatch() {
    return JobTasksMatch.newEmptyMatch();
  }
  
  @Override
  public JobTasksMatch newMatch(final Object... parameters) {
    return JobTasksMatch.newMatch((system.Job) parameters[0], (process.Task) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static JobTasksQuerySpecification INSTANCE = make();
    
    public static JobTasksQuerySpecification make() {
      return new JobTasksQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static JobTasksQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
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
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_Job = body.getOrCreateVariableByName("Job");
      	PVariable var_Task = body.getOrCreateVariableByName("Task");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_Job, "Job"),
      				
      		new ExportedParameter(body, var_Task, "Task")
      	));
      	new TypeBinary(body, CONTEXT, var_Job, var_Task, getFeatureLiteral("http://system/1.0", "Job", "tasks"), "http://system/1.0/Job.tasks");
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
