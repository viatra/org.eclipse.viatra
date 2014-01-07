package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.testing.queries.CorrespondingRecordsMatcher;

/**
 * A pattern-specific query specification that can instantiate CorrespondingRecordsMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordsMatcher
 * @see CorrespondingRecordsMatch
 * 
 */
@SuppressWarnings("all")
public final class CorrespondingRecordsQuerySpecification extends BaseGeneratedQuerySpecification<CorrespondingRecordsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static CorrespondingRecordsQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected CorrespondingRecordsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return CorrespondingRecordsMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.CorrespondingRecords";
    
  }
  
  private CorrespondingRecordsQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<CorrespondingRecordsQuerySpecification> {
    @Override
    public CorrespondingRecordsQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static CorrespondingRecordsQuerySpecification INSTANCE = make();
    
    public static CorrespondingRecordsQuerySpecification make() {
      try {
      	return new CorrespondingRecordsQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
