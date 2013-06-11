package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.NextActivityMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate NextActivityMatcher in a type-safe way.
 * 
 * @see NextActivityMatcher
 * @see NextActivityMatch
 * 
 */
public final class NextActivityQuerySpecification extends BaseGeneratedQuerySpecification<NextActivityMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static NextActivityQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected NextActivityMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return NextActivityMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.nextActivity";
    
  }
  
  private NextActivityQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<NextActivityQuerySpecification> {
    @Override
    public NextActivityQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static NextActivityQuerySpecification INSTANCE = make();
    
    public static NextActivityQuerySpecification make() {
      try {
      	return new NextActivityQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
