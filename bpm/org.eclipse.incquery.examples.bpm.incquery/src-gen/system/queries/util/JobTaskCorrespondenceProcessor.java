package system.queries.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import system.queries.JobTaskCorrespondenceMatch;

/**
 * A match processor tailored for the system.queries.JobTaskCorrespondence pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class JobTaskCorrespondenceProcessor implements IMatchProcessor<JobTaskCorrespondenceMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pJob the value of pattern parameter Job in the currently processed match
   * @param pTask the value of pattern parameter Task in the currently processed match
   * 
   */
  public abstract void process(final EObject pJob, final EObject pTask);
  
  @Override
  public void process(final JobTaskCorrespondenceMatch match) {
    process(match.getJob(), match.getTask());
  }
}
