package system.queries.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import system.queries.TransitiveAffectedTasksThroughDataMatcher;

/**
 * A pattern-specific query specification that can instantiate TransitiveAffectedTasksThroughDataMatcher in a type-safe way.
 * 
 * @see TransitiveAffectedTasksThroughDataMatcher
 * @see TransitiveAffectedTasksThroughDataMatch
 * 
 */
public final class TransitiveAffectedTasksThroughDataQuerySpecification extends BaseGeneratedQuerySpecification<TransitiveAffectedTasksThroughDataMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TransitiveAffectedTasksThroughDataQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected TransitiveAffectedTasksThroughDataMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TransitiveAffectedTasksThroughDataMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "system.queries.TransitiveAffectedTasksThroughData";
    
  }
  
  private TransitiveAffectedTasksThroughDataQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<TransitiveAffectedTasksThroughDataQuerySpecification> {
    @Override
    public TransitiveAffectedTasksThroughDataQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static TransitiveAffectedTasksThroughDataQuerySpecification INSTANCE = make();
    
    public static TransitiveAffectedTasksThroughDataQuerySpecification make() {
      try {
      	return new TransitiveAffectedTasksThroughDataQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
