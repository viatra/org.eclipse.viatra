package operation.queries.util;

import operation.queries.IncorrectEntryInChecklistMatch;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the operation.queries.IncorrectEntryInChecklist pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class IncorrectEntryInChecklistProcessor implements IMatchProcessor<IncorrectEntryInChecklistMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pChecklistEntry the value of pattern parameter ChecklistEntry in the currently processed match
   * @param pTask the value of pattern parameter Task in the currently processed match
   * @param pProcess the value of pattern parameter Process in the currently processed match
   * 
   */
  public abstract void process(final EObject pChecklistEntry, final EObject pTask, final EObject pProcess);
  
  @Override
  public void process(final IncorrectEntryInChecklistMatch match) {
    process(match.getChecklistEntry(), match.getTask(), match.getProcess());
  }
}
