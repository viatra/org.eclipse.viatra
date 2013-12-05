package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;

/**
 * A pattern-specific query specification that can instantiate SingleAuthoredFirstBooksMatcher in a type-safe way.
 * 
 * @see SingleAuthoredFirstBooksMatcher
 * @see SingleAuthoredFirstBooksMatch
 * 
 */
@SuppressWarnings("all")
public final class SingleAuthoredFirstBooksQuerySpecification extends BaseGeneratedQuerySpecification<SingleAuthoredFirstBooksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SingleAuthoredFirstBooksQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SingleAuthoredFirstBooksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SingleAuthoredFirstBooksMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.singleAuthoredFirstBooks";
    
  }
  
  private SingleAuthoredFirstBooksQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<SingleAuthoredFirstBooksQuerySpecification> {
    @Override
    public SingleAuthoredFirstBooksQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SingleAuthoredFirstBooksQuerySpecification INSTANCE = make();
    
    public static SingleAuthoredFirstBooksQuerySpecification make() {
      try {
      	return new SingleAuthoredFirstBooksQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
