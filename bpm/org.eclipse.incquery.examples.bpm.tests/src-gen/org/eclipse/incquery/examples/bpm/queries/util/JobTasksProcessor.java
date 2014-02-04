package org.eclipse.incquery.examples.bpm.queries.util;

import org.eclipse.incquery.examples.bpm.queries.JobTasksMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;
import system.Job;

/**
 * A match processor tailored for the org.eclipse.incquery.examples.bpm.queries.jobTasks pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class JobTasksProcessor implements IMatchProcessor<JobTasksMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pJob the value of pattern parameter Job in the currently processed match
   * @param pTask the value of pattern parameter Task in the currently processed match
   * 
   */
  public abstract void process(final Job pJob, final Task pTask);
  
  @Override
  public void process(final JobTasksMatch match) {
    process(match.getJob(), match.getTask());
    
  }
}
