package system.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.Data;
import system.queries.DataTaskReadCorrespondenceMatch;

/**
 * A match processor tailored for the system.queries.DataTaskReadCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class DataTaskReadCorrespondenceProcessor implements IMatchProcessor<DataTaskReadCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pData the value of pattern parameter Data in the currently processed match 
   * @param pTask the value of pattern parameter Task in the currently processed match 
   * 
   */
  public abstract void process(final Data pData, final Task pTask);
  
  @Override
  public void process(final DataTaskReadCorrespondenceMatch match) {
    process(match.getData(), match.getTask());
    
  }
}
