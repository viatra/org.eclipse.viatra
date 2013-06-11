package operation.queries.util;

import operation.ChecklistEntry;
import operation.queries.DataReadByChecklistEntryMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.Data;

/**
 * A match processor tailored for the operation.queries.DataReadByChecklistEntry pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class DataReadByChecklistEntryProcessor implements IMatchProcessor<DataReadByChecklistEntryMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pCLE the value of pattern parameter CLE in the currently processed match 
   * @param pTask the value of pattern parameter Task in the currently processed match 
   * @param pData the value of pattern parameter Data in the currently processed match 
   * 
   */
  public abstract void process(final ChecklistEntry pCLE, final Task pTask, final Data pData);
  
  @Override
  public void process(final DataReadByChecklistEntryMatch match) {
    process(match.getCLE(), match.getTask(), match.getData());  				
    
  }
}
