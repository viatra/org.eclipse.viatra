package operation.queries.util;

import operation.queries.ChecklistEntryJobCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate ChecklistEntryJobCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistEntryJobCorrespondenceMatcher
 * @see ChecklistEntryJobCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistEntryJobCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<ChecklistEntryJobCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistEntryJobCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ChecklistEntryJobCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryJobCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "operation.queries.ChecklistEntryJobCorrespondence";
    
  }
  
  private ChecklistEntryJobCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ChecklistEntryJobCorrespondenceQuerySpecification> {
    @Override
    public ChecklistEntryJobCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ChecklistEntryJobCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistEntryJobCorrespondenceQuerySpecification make() {
      try {
      	return new ChecklistEntryJobCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
