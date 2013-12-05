package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.BookAuthorsMatcher;

/**
 * A pattern-specific query specification that can instantiate BookAuthorsMatcher in a type-safe way.
 * 
 * @see BookAuthorsMatcher
 * @see BookAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class BookAuthorsQuerySpecification extends BaseGeneratedQuerySpecification<BookAuthorsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static BookAuthorsQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected BookAuthorsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return BookAuthorsMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.bookAuthors";
    
  }
  
  private BookAuthorsQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<BookAuthorsQuerySpecification> {
    @Override
    public BookAuthorsQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static BookAuthorsQuerySpecification INSTANCE = make();
    
    public static BookAuthorsQuerySpecification make() {
      try {
      	return new BookAuthorsQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
