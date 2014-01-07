package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.testing.queries.IncorrectSubstitutionMatcher;

/**
 * A pattern-specific query specification that can instantiate IncorrectSubstitutionMatcher in a type-safe way.
 * 
 * @see IncorrectSubstitutionMatcher
 * @see IncorrectSubstitutionMatch
 * 
 */
@SuppressWarnings("all")
public final class IncorrectSubstitutionQuerySpecification extends BaseGeneratedQuerySpecification<IncorrectSubstitutionMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IncorrectSubstitutionQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected IncorrectSubstitutionMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IncorrectSubstitutionMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.IncorrectSubstitution";
    
  }
  
  private IncorrectSubstitutionQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<IncorrectSubstitutionQuerySpecification> {
    @Override
    public IncorrectSubstitutionQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static IncorrectSubstitutionQuerySpecification INSTANCE = make();
    
    public static IncorrectSubstitutionQuerySpecification make() {
      try {
      	return new IncorrectSubstitutionQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
