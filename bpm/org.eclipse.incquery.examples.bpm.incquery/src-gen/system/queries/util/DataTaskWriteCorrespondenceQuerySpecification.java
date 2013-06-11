package system.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import system.queries.DataTaskWriteCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate DataTaskWriteCorrespondenceMatcher in a type-safe way.
 * 
 * @see DataTaskWriteCorrespondenceMatcher
 * @see DataTaskWriteCorrespondenceMatch
 * 
 */
public final class DataTaskWriteCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<DataTaskWriteCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static DataTaskWriteCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected DataTaskWriteCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return DataTaskWriteCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "system.queries.DataTaskWriteCorrespondence";
    
  }
  
  private DataTaskWriteCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<DataTaskWriteCorrespondenceQuerySpecification> {
    @Override
    public DataTaskWriteCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static DataTaskWriteCorrespondenceQuerySpecification INSTANCE = make();
    
    public static DataTaskWriteCorrespondenceQuerySpecification make() {
      try {
      	return new DataTaskWriteCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
