package operation.queries.util;

import operation.queries.DataReadByChecklistEntryMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate DataReadByChecklistEntryMatcher in a type-safe way.
 * 
 * @see DataReadByChecklistEntryMatcher
 * @see DataReadByChecklistEntryMatch
 * 
 */
public final class DataReadByChecklistEntryQuerySpecification extends BaseGeneratedQuerySpecification<DataReadByChecklistEntryMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static DataReadByChecklistEntryQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected DataReadByChecklistEntryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return DataReadByChecklistEntryMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "operation.queries.DataReadByChecklistEntry";
    
  }
  
  private DataReadByChecklistEntryQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<DataReadByChecklistEntryQuerySpecification> {
    @Override
    public DataReadByChecklistEntryQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static DataReadByChecklistEntryQuerySpecification INSTANCE = make();
    
    public static DataReadByChecklistEntryQuerySpecification make() {
      try {
      	return new DataReadByChecklistEntryQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
