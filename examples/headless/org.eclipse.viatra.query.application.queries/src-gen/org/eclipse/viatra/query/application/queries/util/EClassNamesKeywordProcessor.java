package org.eclipse.viatra.query.application.queries.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.application.queries.EClassNamesKeywordMatch;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the org.eclipse.viatra.query.application.queries.eClassNamesKeyword pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EClassNamesKeywordProcessor implements IMatchProcessor<EClassNamesKeywordMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pC the value of pattern parameter c in the currently processed match
   * @param pN the value of pattern parameter n in the currently processed match
   * 
   */
  public abstract void process(final EClass pC, final String pN);
  
  @Override
  public void process(final EClassNamesKeywordMatch match) {
    process(match.getC(), match.getN());
  }
}
