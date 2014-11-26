package org.eclipse.incquery.examples.bpm.queries.util;

import operation.ChecklistEntry;
import org.eclipse.incquery.examples.bpm.queries.EntryTaskMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import process.Task;

/**
 * A match processor tailored for the org.eclipse.incquery.examples.bpm.queries.entryTask pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EntryTaskProcessor implements IMatchProcessor<EntryTaskMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pEntry the value of pattern parameter Entry in the currently processed match
   * @param pTask the value of pattern parameter Task in the currently processed match
   * 
   */
  public abstract void process(final ChecklistEntry pEntry, final Task pTask);
  
  @Override
  public void process(final EntryTaskMatch match) {
    process(match.getEntry(), match.getTask());
  }
}
