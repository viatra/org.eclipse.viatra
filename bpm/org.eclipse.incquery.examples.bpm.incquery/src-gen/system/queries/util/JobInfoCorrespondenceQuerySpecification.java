package system.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import system.queries.JobInfoCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate JobInfoCorrespondenceMatcher in a type-safe way.
 * 
 * @see JobInfoCorrespondenceMatcher
 * @see JobInfoCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class JobInfoCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<JobInfoCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static JobInfoCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected JobInfoCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return JobInfoCorrespondenceMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "system.queries.JobInfoCorrespondence";
    
  }
  
  private JobInfoCorrespondenceQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<JobInfoCorrespondenceQuerySpecification> {
    @Override
    public JobInfoCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static JobInfoCorrespondenceQuerySpecification INSTANCE = make();
    
    public static JobInfoCorrespondenceQuerySpecification make() {
      try {
      	return new JobInfoCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
