package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.testing.queries.UnexpectedMatchRecord pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class UnexpectedMatchRecordProcessor implements IMatchProcessor<UnexpectedMatchRecordMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pActualSet the value of pattern parameter ActualSet in the currently processed match
   * @param pExpectedSet the value of pattern parameter ExpectedSet in the currently processed match
   * @param pRecord the value of pattern parameter Record in the currently processed match
   * 
   */
  public abstract void process(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord);
  
  @Override
  public void process(final UnexpectedMatchRecordMatch match) {
    process(match.getActualSet(), match.getExpectedSet(), match.getRecord());
    
  }
}
