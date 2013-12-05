package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatcher;

/**
 * A pattern-specific query specification that can instantiate RequestCountOfLibraryMatcher in a type-safe way.
 * 
 * @see RequestCountOfLibraryMatcher
 * @see RequestCountOfLibraryMatch
 * 
 */
@SuppressWarnings("all")
public final class RequestCountOfLibraryQuerySpecification extends BaseGeneratedQuerySpecification<RequestCountOfLibraryMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static RequestCountOfLibraryQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected RequestCountOfLibraryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return RequestCountOfLibraryMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.requestCountOfLibrary";
    
  }
  
  private RequestCountOfLibraryQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<RequestCountOfLibraryQuerySpecification> {
    @Override
    public RequestCountOfLibraryQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static RequestCountOfLibraryQuerySpecification INSTANCE = make();
    
    public static RequestCountOfLibraryQuerySpecification make() {
      try {
      	return new RequestCountOfLibraryQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
