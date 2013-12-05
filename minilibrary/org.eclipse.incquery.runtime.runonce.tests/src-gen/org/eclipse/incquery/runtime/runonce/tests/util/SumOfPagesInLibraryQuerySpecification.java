package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.SumOfPagesInLibraryMatcher;

/**
 * A pattern-specific query specification that can instantiate SumOfPagesInLibraryMatcher in a type-safe way.
 * 
 * @see SumOfPagesInLibraryMatcher
 * @see SumOfPagesInLibraryMatch
 * 
 */
@SuppressWarnings("all")
public final class SumOfPagesInLibraryQuerySpecification extends BaseGeneratedQuerySpecification<SumOfPagesInLibraryMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SumOfPagesInLibraryQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SumOfPagesInLibraryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SumOfPagesInLibraryMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.sumOfPagesInLibrary";
    
  }
  
  private SumOfPagesInLibraryQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<SumOfPagesInLibraryQuerySpecification> {
    @Override
    public SumOfPagesInLibraryQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SumOfPagesInLibraryQuerySpecification INSTANCE = make();
    
    public static SumOfPagesInLibraryQuerySpecification make() {
      try {
      	return new SumOfPagesInLibraryQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
