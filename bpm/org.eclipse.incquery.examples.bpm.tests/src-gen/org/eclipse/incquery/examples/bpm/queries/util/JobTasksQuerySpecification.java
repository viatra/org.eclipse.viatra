package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.JobTasksMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

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
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.jobTasks";
    
  }
  
  private JobTasksQuerySpecification() throws IncQueryException {
    super();
  }
  
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
