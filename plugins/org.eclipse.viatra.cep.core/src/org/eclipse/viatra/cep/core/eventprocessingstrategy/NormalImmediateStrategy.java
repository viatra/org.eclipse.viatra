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

import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.engine.IEventModelManager;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.events.Event;

public class NormalImmediateStrategy extends AbstractImmediateStrategy {

    @Override
    public EventContext getContext() {
        return EventContext.IMMEDIATE;
    }

    public NormalImmediateStrategy(IEventModelManager eventModelManager) {
        super(eventModelManager);
    }

    @Override
    public void fireTransition(Transition t, EventToken eventTokenToMove, Event e) {
        State preState = t.getPreState();
        if (preState instanceof FinalState) {
            return;
        }

        InternalModel model = getEventModelManager().getModel();
        State nextState = t.getPostState();

        if (!handleTimeConstraints(eventTokenToMove, nextState)) {
            return;
        }
        eventTokenToMove.getRecordedEvents().add(model.getLatestEvent());
        preState.setLastProcessedEvent(model.getLatestEvent());
        eventTokenToMove.setCurrentState(nextState);
        getEventModelManager().callbackOnFiredToken(t, eventTokenToMove);
    }

    @Override
    public void handleInitTokenCreation(InternalModel model, final AutomatonFactory factory,
            IObservableComplexEventPattern observedComplexEventPattern) {
        for (Automaton automaton : model.getAutomata()) {
            for (State s : automaton.getStates()) {
                if (s instanceof InitState) {
                    if (s.getEventTokens().isEmpty()) {
                        EventToken cv = factory.createEventToken();
                        cv.setCurrentState(s);
                        model.getEventTokens().add(cv);
                    }
                    break;
                }
            }
        }
    }
}