package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatcher;

/**
 * A pattern-specific query specification that can instantiate UnexpectedMatchRecordMatcher in a type-safe way.
 * 
 * @see UnexpectedMatchRecordMatcher
 * @see UnexpectedMatchRecordMatch
 * 
 */
@SuppressWarnings("all")
public final class UnexpectedMatchRecordQuerySpecification extends BaseGeneratedQuerySpecification<UnexpectedMatchRecordMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static UnexpectedMatchRecordQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected UnexpectedMatchRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return UnexpectedMatchRecordMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.UnexpectedMatchRecord";
    
  }
  
  private UnexpectedMatchRecordQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<UnexpectedMatchRecordQuerySpecification> {
    @Override
    public UnexpectedMatchRecordQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static UnexpectedMatchRecordQuerySpecification INSTANCE = make();
    
    public static UnexpectedMatchRecordQuerySpecification make() {
      try {
      	return new UnexpectedMatchRecordQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
