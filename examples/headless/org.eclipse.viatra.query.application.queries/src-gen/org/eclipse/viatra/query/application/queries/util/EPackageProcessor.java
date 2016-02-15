package org.eclipse.viatra.query.application.queries.util;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.EPackageMatch;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the org.eclipse.viatra.query.application.queries.ePackage pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EPackageProcessor implements IMatchProcessor<EPackageMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pP the value of pattern parameter p in the currently processed match
   * 
   */
  public abstract void process(final EPackage pP);
  
  @Override
  public void process(final EPackageMatch match) {
    process(match.getP());
  }
}
