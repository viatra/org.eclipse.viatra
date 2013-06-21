package operation.queries.util;

import operation.ChecklistEntry;
import operation.queries.TaskChecklistEntryJobCorrespondenceMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.Job;

/**
 * A match processor tailored for the operation.queries.TaskChecklistEntryJobCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class TaskChecklistEntryJobCorrespondenceProcessor implements IMatchProcessor<TaskChecklistEntryJobCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pTask the value of pattern parameter Task in the currently processed match 
   * @param pCLE the value of pattern parameter CLE in the currently processed match 
   * @param pJob the value of pattern parameter Job in the currently processed match 
   * 
   */
  public abstract void process(final Task pTask, final ChecklistEntry pCLE, final Job pJob);
  
  @Override
  public void process(final TaskChecklistEntryJobCorrespondenceMatch match) {
    process(match.getTask(), match.getCLE(), match.getJob());
    
  }
}
