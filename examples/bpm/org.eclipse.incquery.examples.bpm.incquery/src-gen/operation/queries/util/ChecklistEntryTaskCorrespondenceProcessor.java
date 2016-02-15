package operation.queries.util;

import operation.ChecklistEntry;
import operation.queries.ChecklistEntryTaskCorrespondenceMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;

/**
 * A match processor tailored for the operation.queries.ChecklistEntryTaskCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ChecklistEntryTaskCorrespondenceProcessor implements IMatchProcessor<ChecklistEntryTaskCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pCLE the value of pattern parameter CLE in the currently processed match
   * @param pTask the value of pattern parameter Task in the currently processed match
   * 
   */
  public abstract void process(final ChecklistEntry pCLE, final Task pTask);
  
  @Override
  public void process(final ChecklistEntryTaskCorrespondenceMatch match) {
    process(match.getCLE(), match.getTask());
  }
}
