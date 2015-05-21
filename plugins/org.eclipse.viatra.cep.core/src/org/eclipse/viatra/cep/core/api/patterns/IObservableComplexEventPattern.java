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

package org.eclipse.viatra.cep.core.api.patterns;

import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.ParameterTable;
import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

/**
 * Interface for potentially observable {@link EventPattern}s.
 * 
 * @author Istvan David
 * 
 */
public interface IObservableComplexEventPattern {

    Automaton getAutomaton();

    EventPattern getObservableEventPattern();

    List<Event> getObservedAtomicEventInstances();

    ParameterTable getParameterTable();

}