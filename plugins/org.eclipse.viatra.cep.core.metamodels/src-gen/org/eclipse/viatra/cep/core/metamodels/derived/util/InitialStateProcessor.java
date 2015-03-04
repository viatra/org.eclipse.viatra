package org.eclipse.viatra.cep.core.metamodels.derived.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.derived.InitialStateMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.cep.core.metamodels.derived.initialState pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class InitialStateProcessor implements IMatchProcessor<InitialStateMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pThis the value of pattern parameter this in the currently processed match
   * @param pInitState the value of pattern parameter initState in the currently processed match
   * 
   */
  public abstract void process(final Automaton pThis, final InitState pInitState);
  
  @Override
  public void process(final InitialStateMatch match) {
    process(match.getThis(), match.getInitState());
  }
}
