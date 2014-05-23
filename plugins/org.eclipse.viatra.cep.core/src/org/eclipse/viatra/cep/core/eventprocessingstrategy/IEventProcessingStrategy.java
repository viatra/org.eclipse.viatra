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
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.events.Event;

public interface IEventProcessingStrategy {
    EventContext getContext();

    void fireTransition(Transition t, EventToken et, Event e);

    void handleInitTokenCreation(InternalModel model, final AutomatonFactory factory,
            IObservableComplexEventPattern observedComplexEventPattern);

    void handleSmResets(InternalModel model, final AutomatonFactory factory);
}