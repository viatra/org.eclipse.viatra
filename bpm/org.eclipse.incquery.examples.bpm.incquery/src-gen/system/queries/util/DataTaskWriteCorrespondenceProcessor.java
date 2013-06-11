package system.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.Data;
import system.queries.DataTaskWriteCorrespondenceMatch;

/**
 * A match processor tailored for the system.queries.DataTaskWriteCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class DataTaskWriteCorrespondenceProcessor implements IMatchProcessor<DataTaskWriteCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pData the value of pattern parameter Data in the currently processed match 
   * @param pTask the value of pattern parameter Task in the currently processed match 
   * 
   */
  public abstract void process(final Data pData, final Task pTask);
  
  @Override
  public void process(final DataTaskWriteCorrespondenceMatch match) {
    process(match.getData(), match.getTask());  				
    
  }
}
