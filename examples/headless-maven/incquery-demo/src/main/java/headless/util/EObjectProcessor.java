package headless.util;

import headless.EObjectMatch;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the headless.eObject pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EObjectProcessor implements IMatchProcessor<EObjectMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pO the value of pattern parameter o in the currently processed match
   * 
   */
  public abstract void process(final EObject pO);
  
  @Override
  public void process(final EObjectMatch match) {
    process(match.getO());
    
  }
}
