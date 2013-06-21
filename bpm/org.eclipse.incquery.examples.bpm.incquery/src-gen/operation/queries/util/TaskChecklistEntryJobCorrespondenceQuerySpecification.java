package operation.queries.util;

import operation.queries.TaskChecklistEntryJobCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate TaskChecklistEntryJobCorrespondenceMatcher in a type-safe way.
 * 
 * @see TaskChecklistEntryJobCorrespondenceMatcher
 * @see TaskChecklistEntryJobCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class TaskChecklistEntryJobCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<TaskChecklistEntryJobCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TaskChecklistEntryJobCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected TaskChecklistEntryJobCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TaskChecklistEntryJobCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "operation.queries.TaskChecklistEntryJobCorrespondence";
    
  }
  
  private TaskChecklistEntryJobCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<TaskChecklistEntryJobCorrespondenceQuerySpecification> {
    @Override
    public TaskChecklistEntryJobCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static TaskChecklistEntryJobCorrespondenceQuerySpecification INSTANCE = make();
    
    public static TaskChecklistEntryJobCorrespondenceQuerySpecification make() {
      try {
      	return new TaskChecklistEntryJobCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
