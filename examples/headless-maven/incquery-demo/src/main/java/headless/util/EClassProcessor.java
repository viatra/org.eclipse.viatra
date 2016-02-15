package headless.util;

import headless.EClassMatch;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the headless.eClass pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EClassProcessor implements IMatchProcessor<EClassMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pEc the value of pattern parameter ec in the currently processed match
   * 
   */
  public abstract void process(final EClass pEc);
  
  @Override
  public void process(final EClassMatch match) {
    process(match.getEc());
    
  }
}
