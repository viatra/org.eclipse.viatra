package headless.util;

import headless.SubPackageMatch;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the headless.subPackage pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class SubPackageProcessor implements IMatchProcessor<SubPackageMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pP the value of pattern parameter p in the currently processed match
   * @param pSp the value of pattern parameter sp in the currently processed match
   * 
   */
  public abstract void process(final EPackage pP, final EPackage pSp);
  
  @Override
  public void process(final SubPackageMatch match) {
    process(match.getP(), match.getSp());
    
  }
}
