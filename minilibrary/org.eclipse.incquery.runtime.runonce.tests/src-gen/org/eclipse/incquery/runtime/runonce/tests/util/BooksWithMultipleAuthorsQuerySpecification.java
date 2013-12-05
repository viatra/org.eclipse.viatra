package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;

/**
 * A pattern-specific query specification that can instantiate BooksWithMultipleAuthorsMatcher in a type-safe way.
 * 
 * @see BooksWithMultipleAuthorsMatcher
 * @see BooksWithMultipleAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class BooksWithMultipleAuthorsQuerySpecification extends BaseGeneratedQuerySpecification<BooksWithMultipleAuthorsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static BooksWithMultipleAuthorsQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected BooksWithMultipleAuthorsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return BooksWithMultipleAuthorsMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors";
    
  }
  
  private BooksWithMultipleAuthorsQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<BooksWithMultipleAuthorsQuerySpecification> {
    @Override
    public BooksWithMultipleAuthorsQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static BooksWithMultipleAuthorsQuerySpecification INSTANCE = make();
    
    public static BooksWithMultipleAuthorsQuerySpecification make() {
      try {
      	return new BooksWithMultipleAuthorsQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
