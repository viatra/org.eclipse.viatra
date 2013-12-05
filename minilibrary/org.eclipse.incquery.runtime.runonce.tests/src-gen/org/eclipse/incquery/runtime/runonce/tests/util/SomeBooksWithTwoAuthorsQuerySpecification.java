package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatcher;

/**
 * A pattern-specific query specification that can instantiate SomeBooksWithTwoAuthorsMatcher in a type-safe way.
 * 
 * @see SomeBooksWithTwoAuthorsMatcher
 * @see SomeBooksWithTwoAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class SomeBooksWithTwoAuthorsQuerySpecification extends BaseGeneratedQuerySpecification<SomeBooksWithTwoAuthorsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SomeBooksWithTwoAuthorsQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SomeBooksWithTwoAuthorsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SomeBooksWithTwoAuthorsMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.someBooksWithTwoAuthors";
    
  }
  
  private SomeBooksWithTwoAuthorsQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<SomeBooksWithTwoAuthorsQuerySpecification> {
    @Override
    public SomeBooksWithTwoAuthorsQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SomeBooksWithTwoAuthorsQuerySpecification INSTANCE = make();
    
    public static SomeBooksWithTwoAuthorsQuerySpecification make() {
      try {
      	return new SomeBooksWithTwoAuthorsQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
