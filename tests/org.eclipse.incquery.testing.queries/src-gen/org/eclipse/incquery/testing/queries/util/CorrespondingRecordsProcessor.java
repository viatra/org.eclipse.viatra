package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.testing.queries.CorrespondingRecordsMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.testing.queries.CorrespondingRecords pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class CorrespondingRecordsProcessor implements IMatchProcessor<CorrespondingRecordsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pCorrespondingRecord the value of pattern parameter CorrespondingRecord in the currently processed match 
   * 
   */
  public abstract void process(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord);
  
  @Override
  public void process(final CorrespondingRecordsMatch match) {
    process(match.getRecord(), match.getCorrespondingRecord());
    
  }
}
