package org.eclipse.viatra.query.application.queries.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.application.queries.EClassMatch;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the org.eclipse.viatra.query.application.queries.eClass pattern.
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
