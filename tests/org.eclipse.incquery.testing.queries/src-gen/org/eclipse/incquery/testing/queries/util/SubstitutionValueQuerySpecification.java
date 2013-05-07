package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatcher;

/**
 * A pattern-specific query specification that can instantiate SubstitutionValueMatcher in a type-safe way.
 * 
 * @see SubstitutionValueMatcher
 * @see SubstitutionValueMatch
 * 
 */
public final class SubstitutionValueQuerySpecification extends BaseGeneratedQuerySpecification<SubstitutionValueMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SubstitutionValueQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SubstitutionValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SubstitutionValueMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.SubstitutionValue";
    
  }
  
  private SubstitutionValueQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<SubstitutionValueQuerySpecification> {
    @Override
    public SubstitutionValueQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static SubstitutionValueQuerySpecification INSTANCE = make();
    
    public static SubstitutionValueQuerySpecification make() {
      try {
      	return new SubstitutionValueQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
