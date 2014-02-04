package system.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.queries.UndefinedServiceTasksMatch;

/**
 * A match processor tailored for the system.queries.UndefinedServiceTasks pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class UndefinedServiceTasksProcessor implements IMatchProcessor<UndefinedServiceTasksMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pTask the value of pattern parameter Task in the currently processed match
   * 
   */
  public abstract void process(final Task pTask);
  
  @Override
  public void process(final UndefinedServiceTasksMatch match) {
    process(match.getTask());
    
  }
}
