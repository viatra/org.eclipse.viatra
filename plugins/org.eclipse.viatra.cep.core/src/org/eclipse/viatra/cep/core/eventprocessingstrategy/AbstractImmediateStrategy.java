/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.eventprocessingstrategy;

import org.apache.log4j.Logger;
import org.eclipse.viatra.cep.core.engine.IEventModelManager;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState;

public abstract class AbstractImmediateStrategy extends AbstractEventProcessingStrategy {

    private final Logger logger = LoggerUtils.getInstance().getLogger();

    public AbstractImmediateStrategy(IEventModelManager eventModelManager) {
        super(eventModelManager);
    }

    @Override
    public void handleSmResets(InternalModel model, final AutomatonFactory factory) {
        for (Automaton automaton : model.getAutomata()) {
            if (needsReset(automaton)) {
                String id = automaton.getEventPattern().getId();
                logger.debug("ImmediateStrategy: No suitable update in the SM : " + id + ". It's going to be reset.");

                for (State state : automaton.getStates()) {
                    if ((state instanceof InitState) || (state instanceof TrapState) || (state instanceof FinalState)) {
                        continue;
                    }

                    if (state.getEventTokens().isEmpty()) {
                        continue;
                    }

                    logger.debug("ImmediateStrategy: Deleting tokens from state: " + state.getLabel());

                    state.getEventTokens().clear();
                }

                model.setLatestEvent(null);

                InitState initState = getEventModelManager().getInitStatesForAutomata().get(automaton);
                if (initState.getEventTokens().isEmpty()) {
                    EventToken cv = factory.createEventToken();
                    cv.setCurrentState(initState);
                    model.getEventTokens().add(cv);
                }
            }
        }

    }

    private boolean needsReset(Automaton automaton) {
        return !(getEventModelManager().getWasEnabledForTheLatestEvent().containsKey(automaton));
    }

}
