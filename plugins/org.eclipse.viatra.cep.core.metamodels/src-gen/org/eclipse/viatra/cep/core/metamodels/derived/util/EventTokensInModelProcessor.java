package org.eclipse.viatra.cep.core.metamodels.derived.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.derived.EventTokensInModelMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.cep.core.metamodels.derived.eventTokensInModel pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class EventTokensInModelProcessor implements IMatchProcessor<EventTokensInModelMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pThis the value of pattern parameter this in the currently processed match
   * @param pEventToken the value of pattern parameter eventToken in the currently processed match
   * 
   */
  public abstract void process(final InternalModel pThis, final EventToken pEventToken);
  
  @Override
  public void process(final EventTokensInModelMatch match) {
    process(match.getThis(), match.getEventToken());
  }
}
