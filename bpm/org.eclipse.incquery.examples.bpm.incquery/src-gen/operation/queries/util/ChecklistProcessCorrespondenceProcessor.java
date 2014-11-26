package operation.queries.util;

import operation.Checklist;
import operation.queries.ChecklistProcessCorrespondenceMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the operation.queries.ChecklistProcessCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ChecklistProcessCorrespondenceProcessor implements IMatchProcessor<ChecklistProcessCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pChecklist the value of pattern parameter Checklist in the currently processed match
   * @param pProcess the value of pattern parameter Process in the currently processed match
   * 
   */
  public abstract void process(final Checklist pChecklist, final process.Process pProcess);
  
  @Override
  public void process(final ChecklistProcessCorrespondenceMatch match) {
    process(match.getChecklist(), match.getProcess());
  }
}
