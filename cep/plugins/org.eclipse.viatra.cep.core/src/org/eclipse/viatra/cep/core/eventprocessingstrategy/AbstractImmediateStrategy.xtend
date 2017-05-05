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
package org.eclipse.viatra.cep.core.eventprocessingstrategy

import org.eclipse.viatra.cep.core.engine.IEventModelManager
import org.eclipse.viatra.cep.core.logging.LoggerUtils
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.State

import static extension org.eclipse.viatra.cep.core.utils.AutomatonUtils.*
import org.apache.log4j.Logger

/**
 * Common ancestor of the <i>immediate</i> type of strategies. Defines how the {@link Automaton} resets should be
 * handled in general.
 * 
 * @author Istvan David
 * 
 */
abstract class AbstractImmediateStrategy extends AbstractStrategy {
    val extension Logger logger = LoggerUtils.instance.logger;

    new(IEventModelManager eventModelManager) {
        super(eventModelManager)
    }

    override public handleAutomatonResets(InternalModel model, AutomatonFactory factory) {
        model.automata.filter[automaton|automaton.needsReset].forEach [ automaton |
            debug(String.format("ImmediateStrategy: No update in automaton: %s. Resetting automaton.", automaton.id));
            automaton.normalStates.filter[state|state.notEmpty].forEach [ state |
                debug(String.format("ImmediateStrategy: Deleting tokens from state: %s.", state.prettyLabel))
                state.clear
            ]

            var initState = automaton.initialState
            if (initState.empty) {
                newEventToken(automaton, initState)
            }
        ]
    }

    def private prettyLabel(State state) {
        if (state.label.nullOrEmpty) {
            return state.toString
        }
        state.label
    }

    def private clear(State state) {
        state.eventTokens.clear
    }

    def private id(Automaton automaton) {
        automaton.eventPatternId
    }

    def private needsReset(Automaton automaton) {
        !(eventModelManager.enabledAutomataForTheLatestEvent.contains(automaton));
    }
}
