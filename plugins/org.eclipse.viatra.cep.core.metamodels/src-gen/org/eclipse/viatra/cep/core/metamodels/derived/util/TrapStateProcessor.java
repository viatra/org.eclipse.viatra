package org.eclipse.viatra.cep.core.metamodels.derived.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState;
import org.eclipse.viatra.cep.core.metamodels.derived.TrapStateMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.cep.core.metamodels.derived.trapState pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class TrapStateProcessor implements IMatchProcessor<TrapStateMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pThis the value of pattern parameter this in the currently processed match
   * @param pTrapState the value of pattern parameter trapState in the currently processed match
   * 
   */
  public abstract void process(final Automaton pThis, final TrapState pTrapState);
  
  @Override
  public void process(final TrapStateMatch match) {
    process(match.getThis(), match.getTrapState());
  }
}
