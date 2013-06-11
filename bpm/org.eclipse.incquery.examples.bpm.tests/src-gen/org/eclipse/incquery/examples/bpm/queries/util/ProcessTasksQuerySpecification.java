package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.ProcessTasksMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate ProcessTasksMatcher in a type-safe way.
 * 
 * @see ProcessTasksMatcher
 * @see ProcessTasksMatch
 * 
 */
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
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.processTasks";
    
  }
  
  private ProcessTasksQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<ProcessTasksQuerySpecification> {
    @Override
    public ProcessTasksQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
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
