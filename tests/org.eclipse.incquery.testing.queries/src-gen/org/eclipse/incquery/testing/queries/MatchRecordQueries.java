package org.eclipse.incquery.testing.queries;

import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.testing.queries.RecordRoleValueMatcher;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatcher;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatcher;

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
    querySpecifications.add(UnexpectedMatchRecordMatcher.querySpecification());
    querySpecifications.add(RecordRoleValueMatcher.querySpecification());
    querySpecifications.add(SubstitutionValueMatcher.querySpecification());
    
  }
}
