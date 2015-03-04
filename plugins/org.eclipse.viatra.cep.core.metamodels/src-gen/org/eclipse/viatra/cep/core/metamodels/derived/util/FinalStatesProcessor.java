package org.eclipse.viatra.cep.core.metamodels.derived.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.derived.FinalStatesMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.cep.core.metamodels.derived.finalStates pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class FinalStatesProcessor implements IMatchProcessor<FinalStatesMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pThis the value of pattern parameter this in the currently processed match
   * @param pFinalState the value of pattern parameter finalState in the currently processed match
   * 
   */
  public abstract void process(final Automaton pThis, final FinalState pFinalState);
  
  @Override
  public void process(final FinalStatesMatch match) {
    process(match.getThis(), match.getFinalState());
  }
}
