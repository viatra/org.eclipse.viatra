package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;

/**
 * A pattern-specific query specification that can instantiate LongSciFiBooksOfAuthorMatcher in a type-safe way.
 * 
 * @see LongSciFiBooksOfAuthorMatcher
 * @see LongSciFiBooksOfAuthorMatch
 * 
 */
@SuppressWarnings("all")
public final class LongSciFiBooksOfAuthorQuerySpecification extends BaseGeneratedQuerySpecification<LongSciFiBooksOfAuthorMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static LongSciFiBooksOfAuthorQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected LongSciFiBooksOfAuthorMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return LongSciFiBooksOfAuthorMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.runtime.runonce.tests";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.longSciFiBooksOfAuthor";
    
  }
  
  private LongSciFiBooksOfAuthorQuerySpecification() throws IncQueryException {
    super();
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<LongSciFiBooksOfAuthorQuerySpecification> {
    @Override
    public LongSciFiBooksOfAuthorQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static LongSciFiBooksOfAuthorQuerySpecification INSTANCE = make();
    
    public static LongSciFiBooksOfAuthorQuerySpecification make() {
      try {
      	return new LongSciFiBooksOfAuthorQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
