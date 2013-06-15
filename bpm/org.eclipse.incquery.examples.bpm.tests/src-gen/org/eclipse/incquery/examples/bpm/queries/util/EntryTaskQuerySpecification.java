package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.EntryTaskMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate EntryTaskMatcher in a type-safe way.
 * 
 * @see EntryTaskMatcher
 * @see EntryTaskMatch
 * 
 */
public final class EntryTaskQuerySpecification extends BaseGeneratedQuerySpecification<EntryTaskMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static EntryTaskQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected EntryTaskMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return EntryTaskMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.entryTask";
    
  }
  
  private EntryTaskQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<EntryTaskQuerySpecification> {
    @Override
    public EntryTaskQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static EntryTaskQuerySpecification INSTANCE = make();
    
    public static EntryTaskQuerySpecification make() {
      try {
      	return new EntryTaskQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
