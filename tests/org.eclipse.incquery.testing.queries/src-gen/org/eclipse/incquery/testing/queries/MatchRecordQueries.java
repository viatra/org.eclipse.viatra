package org.eclipse.incquery.testing.queries;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecordMatcher;
import org.eclipse.incquery.testing.queries.CorrespondingRecordsMatcher;
import org.eclipse.incquery.testing.queries.IncorrectSubstitutionMatcher;
import org.eclipse.incquery.testing.queries.RecordRoleValueMatcher;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatcher;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatcher;
import org.eclipse.incquery.testing.queries.util.CorrespondingRecordInMatchSetRecordQuerySpecification;
import org.eclipse.incquery.testing.queries.util.CorrespondingRecordsQuerySpecification;
import org.eclipse.incquery.testing.queries.util.IncorrectSubstitutionQuerySpecification;
import org.eclipse.incquery.testing.queries.util.RecordRoleValueQuerySpecification;
import org.eclipse.incquery.testing.queries.util.SubstitutionValueQuerySpecification;
import org.eclipse.incquery.testing.queries.util.UnexpectedMatchRecordQuerySpecification;

/**
 * A pattern group formed of all patterns defined in matchRecordQueries.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file matchRecordQueries.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.incquery.testing.queries, the group contains the definition of the following patterns: <ul>
 * <li>IncorrectSubstitution</li>
 * <li>CorrespondingRecords</li>
 * <li>CorrespondingRecordInMatchSetRecord</li>
 * <li>UnexpectedMatchRecord</li>
 * <li>RecordRoleValue</li>
 * <li>SubstitutionValue</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class MatchRecordQueries extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static MatchRecordQueries instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new MatchRecordQueries();
    }
    return INSTANCE;
    
  }
  
  private static MatchRecordQueries INSTANCE;
  
  private MatchRecordQueries() throws IncQueryException {
    querySpecifications.add(IncorrectSubstitutionQuerySpecification.instance());
    querySpecifications.add(CorrespondingRecordsQuerySpecification.instance());
    querySpecifications.add(CorrespondingRecordInMatchSetRecordQuerySpecification.instance());
    querySpecifications.add(UnexpectedMatchRecordQuerySpecification.instance());
    querySpecifications.add(RecordRoleValueQuerySpecification.instance());
    querySpecifications.add(SubstitutionValueQuerySpecification.instance());
    
  }
  
  public IncorrectSubstitutionQuerySpecification getIncorrectSubstitution() throws IncQueryException {
    return IncorrectSubstitutionQuerySpecification.instance();
  }
  
  public IncorrectSubstitutionMatcher getIncorrectSubstitution(final IncQueryEngine engine) throws IncQueryException {
    return IncorrectSubstitutionMatcher.on(engine);
  }
  
  public CorrespondingRecordsQuerySpecification getCorrespondingRecords() throws IncQueryException {
    return CorrespondingRecordsQuerySpecification.instance();
  }
  
  public CorrespondingRecordsMatcher getCorrespondingRecords(final IncQueryEngine engine) throws IncQueryException {
    return CorrespondingRecordsMatcher.on(engine);
  }
  
  public CorrespondingRecordInMatchSetRecordQuerySpecification getCorrespondingRecordInMatchSetRecord() throws IncQueryException {
    return CorrespondingRecordInMatchSetRecordQuerySpecification.instance();
  }
  
  public CorrespondingRecordInMatchSetRecordMatcher getCorrespondingRecordInMatchSetRecord(final IncQueryEngine engine) throws IncQueryException {
    return CorrespondingRecordInMatchSetRecordMatcher.on(engine);
  }
  
  public UnexpectedMatchRecordQuerySpecification getUnexpectedMatchRecord() throws IncQueryException {
    return UnexpectedMatchRecordQuerySpecification.instance();
  }
  
  public UnexpectedMatchRecordMatcher getUnexpectedMatchRecord(final IncQueryEngine engine) throws IncQueryException {
    return UnexpectedMatchRecordMatcher.on(engine);
  }
  
  public RecordRoleValueQuerySpecification getRecordRoleValue() throws IncQueryException {
    return RecordRoleValueQuerySpecification.instance();
  }
  
  public RecordRoleValueMatcher getRecordRoleValue(final IncQueryEngine engine) throws IncQueryException {
    return RecordRoleValueMatcher.on(engine);
  }
  
  public SubstitutionValueQuerySpecification getSubstitutionValue() throws IncQueryException {
    return SubstitutionValueQuerySpecification.instance();
  }
  
  public SubstitutionValueMatcher getSubstitutionValue(final IncQueryEngine engine) throws IncQueryException {
    return SubstitutionValueMatcher.on(engine);
  }
}
