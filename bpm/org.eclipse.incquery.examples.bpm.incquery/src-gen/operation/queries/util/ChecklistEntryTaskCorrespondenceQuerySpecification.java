package operation.queries.util;

import operation.queries.ChecklistEntryTaskCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate ChecklistEntryTaskCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistEntryTaskCorrespondenceMatcher
 * @see ChecklistEntryTaskCorrespondenceMatch
 * 
 */
public final class ChecklistEntryTaskCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<ChecklistEntryTaskCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistEntryTaskCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ChecklistEntryTaskCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryTaskCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "operation.queries.ChecklistEntryTaskCorrespondence";
    
  }
  
  private ChecklistEntryTaskCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<ChecklistEntryTaskCorrespondenceQuerySpecification> {
    @Override
    public ChecklistEntryTaskCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static ChecklistEntryTaskCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistEntryTaskCorrespondenceQuerySpecification make() {
      try {
      	return new ChecklistEntryTaskCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
