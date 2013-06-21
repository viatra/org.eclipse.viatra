package system.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.queries.TransitiveAffectedTasksThroughDataMatch;

/**
 * A match processor tailored for the system.queries.TransitiveAffectedTasksThroughData pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class TransitiveAffectedTasksThroughDataProcessor implements IMatchProcessor<TransitiveAffectedTasksThroughDataMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pSourceTask the value of pattern parameter SourceTask in the currently processed match 
   * @param pAffectedTask the value of pattern parameter AffectedTask in the currently processed match 
   * 
   */
  public abstract void process(final Task pSourceTask, final Task pAffectedTask);
  
  @Override
  public void process(final TransitiveAffectedTasksThroughDataMatch match) {
    process(match.getSourceTask(), match.getAffectedTask());
    
  }
}
