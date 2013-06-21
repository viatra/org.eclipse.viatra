package operation.queries.util;

import operation.ChecklistEntry;
import operation.queries.ChecklistEntryJobCorrespondenceMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import system.Job;

/**
 * A match processor tailored for the operation.queries.ChecklistEntryJobCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ChecklistEntryJobCorrespondenceProcessor implements IMatchProcessor<ChecklistEntryJobCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pCLE the value of pattern parameter CLE in the currently processed match 
   * @param pJob the value of pattern parameter Job in the currently processed match 
   * 
   */
  public abstract void process(final ChecklistEntry pCLE, final Job pJob);
  
  @Override
  public void process(final ChecklistEntryJobCorrespondenceMatch match) {
    process(match.getCLE(), match.getJob());
    
  }
}
