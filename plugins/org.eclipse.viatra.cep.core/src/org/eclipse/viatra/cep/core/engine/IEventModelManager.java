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

package org.eclipse.viatra.cep.core.engine;

import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.evm.CepRealm;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.streams.EventStream;

public interface IEventModelManager {
    InternalModel getModel();

    ResourceSet getResourceSet();

    CepRealm getCepRealm();

    /**
     * This is a cache functionality to track init states, e.g. in automaton reset scenarios in immediate event
     * processing contexts. Should be replaced by a direct Automaton->InitState reference in the metamodel.
     */
    Map<Automaton, InitState> getInitStatesForAutomata();

    /**
     * This is a cache functionality for quickly finding the Final states of an automaton when removing tokens from
     * there. Should be replaced by a direct Automaton->FinalState reference in the metamodel.
     */
    Map<Automaton, FinalState> getFinalStatesForAutomata();

    Map<Automaton, Boolean> getWasEnabledForTheLatestEvent();

    void registerNewEventStream(EventStream newEventStream);

    void fireTransition(TypedTransition transition, EventToken token);

    void callbackOnFiredToken(Transition t, EventToken eventTokenToMove);

    void callbackOnPatternRecognition(IObservableComplexEventPattern observedPattern);

}
