package system.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import system.queries.JobTaskCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate JobTaskCorrespondenceMatcher in a type-safe way.
 * 
 * @see JobTaskCorrespondenceMatcher
 * @see JobTaskCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class JobTaskCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<JobTaskCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static JobTaskCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected JobTaskCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return JobTaskCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "system.queries.JobTaskCorrespondence";
    
  }
  
  private JobTaskCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<JobTaskCorrespondenceQuerySpecification> {
    @Override
    public JobTaskCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static JobTaskCorrespondenceQuerySpecification INSTANCE = make();
    
    public static JobTaskCorrespondenceQuerySpecification make() {
      try {
      	return new JobTaskCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
