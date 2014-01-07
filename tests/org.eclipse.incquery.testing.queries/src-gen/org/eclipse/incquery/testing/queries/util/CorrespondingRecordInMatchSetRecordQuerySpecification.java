package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecordMatcher;

/**
 * A pattern-specific query specification that can instantiate CorrespondingRecordInMatchSetRecordMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordInMatchSetRecordMatcher
 * @see CorrespondingRecordInMatchSetRecordMatch
 * 
 */
@SuppressWarnings("all")
public final class CorrespondingRecordInMatchSetRecordQuerySpecification extends BaseGeneratedQuerySpecification<CorrespondingRecordInMatchSetRecordMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static CorrespondingRecordInMatchSetRecordQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected CorrespondingRecordInMatchSetRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return CorrespondingRecordInMatchSetRecordMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecord";
    
  }
  
  private CorrespondingRecordInMatchSetRecordQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<CorrespondingRecordInMatchSetRecordQuerySpecification> {
    @Override
    public CorrespondingRecordInMatchSetRecordQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static CorrespondingRecordInMatchSetRecordQuerySpecification INSTANCE = make();
    
    public static CorrespondingRecordInMatchSetRecordQuerySpecification make() {
      try {
      	return new CorrespondingRecordInMatchSetRecordQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
