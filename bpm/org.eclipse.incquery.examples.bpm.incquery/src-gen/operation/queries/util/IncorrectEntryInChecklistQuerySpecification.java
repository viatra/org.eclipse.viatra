package operation.queries.util;

import operation.queries.IncorrectEntryInChecklistMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;

/**
 * A pattern-specific query specification that can instantiate IncorrectEntryInChecklistMatcher in a type-safe way.
 * 
 * @see IncorrectEntryInChecklistMatcher
 * @see IncorrectEntryInChecklistMatch
 * 
 */
public final class IncorrectEntryInChecklistQuerySpecification extends BaseGeneratedQuerySpecification<IncorrectEntryInChecklistMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IncorrectEntryInChecklistQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected IncorrectEntryInChecklistMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IncorrectEntryInChecklistMatcher.on(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.examples.bpm.incquery";
    
  }
  
  @Override
  protected String patternName() {
    return "operation.queries.IncorrectEntryInChecklist";
    
  }
  
  private IncorrectEntryInChecklistQuerySpecification() throws IncQueryException {
    super();
  }
  public static class Provider implements IQuerySpecificationProvider<IncorrectEntryInChecklistQuerySpecification> {
    @Override
    public IncorrectEntryInChecklistQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static IncorrectEntryInChecklistQuerySpecification INSTANCE = make();
    
    public static IncorrectEntryInChecklistQuerySpecification make() {
      try {
      	return new IncorrectEntryInChecklistQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
