package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.ProcessTasksMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Activity;

/**
 * A match processor tailored for the org.eclipse.incquery.examples.bpm.queries.processTasks pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ProcessTasksProcessor implements IMatchProcessor<ProcessTasksMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pProc the value of pattern parameter Proc in the currently processed match
   * @param pTask the value of pattern parameter Task in the currently processed match
   * 
   */
  public abstract void process(final process.Process pProc, final Activity pTask);
  
  @Override
  public void process(final ProcessTasksMatch match) {
    process(match.getProc(), match.getTask());
  }
}
