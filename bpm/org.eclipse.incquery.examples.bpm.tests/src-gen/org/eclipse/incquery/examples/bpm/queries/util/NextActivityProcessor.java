package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.NextActivityMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Activity;

/**
 * A match processor tailored for the org.eclipse.incquery.examples.bpm.queries.nextActivity pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class NextActivityProcessor implements IMatchProcessor<NextActivityMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pAct the value of pattern parameter Act in the currently processed match 
   * @param pNext the value of pattern parameter Next in the currently processed match 
   * 
   */
  public abstract void process(final Activity pAct, final Activity pNext);
  
  @Override
  public void process(final NextActivityMatch match) {
    process(match.getAct(), match.getNext());  				
    
  }
}
