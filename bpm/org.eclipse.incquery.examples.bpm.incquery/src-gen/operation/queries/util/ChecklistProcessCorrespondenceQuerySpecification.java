package operation.queries.util;

import operation.queries.ChecklistProcessCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate ChecklistProcessCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistProcessCorrespondenceMatcher
 * @see ChecklistProcessCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistProcessCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<ChecklistProcessCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistProcessCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ChecklistProcessCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistProcessCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "operation.queries.ChecklistProcessCorrespondence";
    
  }
  
  private ChecklistProcessCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ChecklistProcessCorrespondenceQuerySpecification> {
    @Override
    public ChecklistProcessCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ChecklistProcessCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistProcessCorrespondenceQuerySpecification make() {
      try {
      	return new ChecklistProcessCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
