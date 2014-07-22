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
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;

/**
 * Interface for event processing strategies. An event processing strategy is derived from an {@link EventContext} and
 * should take care of the following tasks at runtime:
 * <ul>
 * <li>firing enabled transitions;
 * <li>recreate {@link EventToken}s in the {@link InitState} of {@link Automaton}s, depending on the semantics of the
 * {@link EventContext};
 * <li>handle situations when resetting the {@link Automaton}s is required by the semantics of the {@link EventContext}.
 * </ul>
 * 
 * @author Istvan David
 * 
 */
public interface IEventProcessingStrategy {
    EventContext getContext();

    void fireTransition(Transition transition, EventToken eventTokenToMove);

    void handleInitTokenCreation(InternalModel model, final AutomatonFactory factory,
            IObservableComplexEventPattern observedComplexEventPattern);

    void handleAutomatonResets(InternalModel model, final AutomatonFactory factory);
}