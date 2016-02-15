package headless.util;

import headless.ClassesInPackageMatch;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the headless.classesInPackage pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ClassesInPackageProcessor implements IMatchProcessor<ClassesInPackageMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pP the value of pattern parameter p in the currently processed match
   * @param pEc the value of pattern parameter ec in the currently processed match
   * 
   */
  public abstract void process(final EPackage pP, final EClass pEc);
  
  @Override
  public void process(final ClassesInPackageMatch match) {
    process(match.getP(), match.getEc());
    
  }
}
