package system.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import system.queries.TasksAffectedThroughDataMatcher;

/**
 * A pattern-specific query specification that can instantiate TasksAffectedThroughDataMatcher in a type-safe way.
 * 
 * @see TasksAffectedThroughDataMatcher
 * @see TasksAffectedThroughDataMatch
 * 
 */
@SuppressWarnings("all")
public final class TasksAffectedThroughDataQuerySpecification extends BaseGeneratedQuerySpecification<TasksAffectedThroughDataMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TasksAffectedThroughDataQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected TasksAffectedThroughDataMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TasksAffectedThroughDataMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "system.queries.TasksAffectedThroughData";
    
  }
  
  private TasksAffectedThroughDataQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<TasksAffectedThroughDataQuerySpecification> {
    @Override
    public TasksAffectedThroughDataQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static TasksAffectedThroughDataQuerySpecification INSTANCE = make();
    
    public static TasksAffectedThroughDataQuerySpecification make() {
      try {
      	return new TasksAffectedThroughDataQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
