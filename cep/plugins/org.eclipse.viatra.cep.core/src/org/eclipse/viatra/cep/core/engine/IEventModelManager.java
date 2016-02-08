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

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.evm.CepRealm;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.streams.EventStream;

public interface IEventModelManager {
    InternalModel getModel();

    ResourceSet getResourceSet();

    CepRealm getCepRealm();

    List<Automaton> getEnabledAutomataForTheLatestEvent();

    void registerNewEventStream(EventStream newEventStream);

    void handleEvent(TypedTransition transition, EventToken token);

    void fireTransition(TypedTransition transition, EventToken token);

    void callbackOnFiredToken(Transition t, EventToken eventTokenToMove);

    void callbackOnPatternRecognition(IObservableComplexEventPattern observedPattern);

}
