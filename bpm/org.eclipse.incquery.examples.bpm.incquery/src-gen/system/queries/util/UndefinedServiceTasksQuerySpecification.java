package system.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import system.queries.UndefinedServiceTasksMatcher;

/**
 * A pattern-specific query specification that can instantiate UndefinedServiceTasksMatcher in a type-safe way.
 * 
 * @see UndefinedServiceTasksMatcher
 * @see UndefinedServiceTasksMatch
 * 
 */
public final class UndefinedServiceTasksQuerySpecification extends BaseGeneratedQuerySpecification<UndefinedServiceTasksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static UndefinedServiceTasksQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected UndefinedServiceTasksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return UndefinedServiceTasksMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "system.queries.UndefinedServiceTasks";
    
  }
  
  private UndefinedServiceTasksQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<UndefinedServiceTasksQuerySpecification> {
    @Override
    public UndefinedServiceTasksQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static UndefinedServiceTasksQuerySpecification INSTANCE = make();
    
    public static UndefinedServiceTasksQuerySpecification make() {
      try {
      	return new UndefinedServiceTasksQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
