package org.eclipse.incquery.testing.queries.recordrolevalue;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcher;

/**
 * A pattern-specific query specification that can instantiate RecordRoleValueMatcher in a type-safe way.
 * 
 * @see RecordRoleValueMatcher
 * @see RecordRoleValueMatch
 * 
 */
public final class RecordRoleValueQuerySpecification extends BaseGeneratedQuerySpecification<RecordRoleValueMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static RecordRoleValueQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected RecordRoleValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return new RecordRoleValueMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.RecordRoleValue";
    
  }
  
  private RecordRoleValueQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<RecordRoleValueQuerySpecification> {
    @Override
    public RecordRoleValueQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static RecordRoleValueQuerySpecification INSTANCE = make();
    
    public static RecordRoleValueQuerySpecification make() {
      try {
      	return new RecordRoleValueQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
