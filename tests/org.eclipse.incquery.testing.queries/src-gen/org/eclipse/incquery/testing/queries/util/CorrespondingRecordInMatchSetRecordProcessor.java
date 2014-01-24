package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecordMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecord pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class CorrespondingRecordInMatchSetRecordProcessor implements IMatchProcessor<CorrespondingRecordInMatchSetRecordMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match
   * @param pCorrespodingRecord the value of pattern parameter CorrespodingRecord in the currently processed match
   * @param pExpectedSet the value of pattern parameter ExpectedSet in the currently processed match
   * 
   */
  public abstract void process(final MatchRecord pRecord, final MatchRecord pCorrespodingRecord, final MatchSetRecord pExpectedSet);
  
  @Override
  public void process(final CorrespondingRecordInMatchSetRecordMatch match) {
    process(match.getRecord(), match.getCorrespodingRecord(), match.getExpectedSet());
    
  }
}
