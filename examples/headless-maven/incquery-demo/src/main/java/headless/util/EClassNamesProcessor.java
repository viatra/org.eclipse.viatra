package headless.util;

import headless.EClassNamesMatch;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the headless.eClassNames pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EClassNamesProcessor implements IMatchProcessor<EClassNamesMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pC the value of pattern parameter c in the currently processed match
   * @param pN the value of pattern parameter n in the currently processed match
   * 
   */
  public abstract void process(final EClass pC, final String pN);
  
  @Override
  public void process(final EClassNamesMatch match) {
    process(match.getC(), match.getN());
    
  }
}
