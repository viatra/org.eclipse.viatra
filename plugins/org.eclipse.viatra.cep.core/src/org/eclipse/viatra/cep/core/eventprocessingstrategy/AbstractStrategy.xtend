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

import com.google.common.base.Preconditions
import org.eclipse.viatra.cep.core.engine.IEventModelManager
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition
import org.eclipse.viatra.cep.core.metamodels.events.Event
import org.eclipse.xtend.lib.annotations.Accessors

import static extension org.eclipse.viatra.cep.core.utils.AutomatonUtils.*

/**
* Common ancestor of the of concrete strategies. Defines how the {@link EventToken}s should be fired on the
* {@link Transition}s and how the new {@link EventToken}s in the {@link InitState}s should be produced in general.
* The inheriting strategies may override this behavior.
*
* @author Istvan David
*
*/
abstract class AbstractStrategy implements IEventProcessingStrategy {
	@Accessors IEventModelManager eventModelManager;

	new(IEventModelManager eventModelManager) {
		this.eventModelManager = eventModelManager
	}

	override handleEvent(Transition transition, EventToken eventTokenToMove) {
		eventTokenToMove.addProcessedEvent(eventModelManager.model.latestEvent)
	}

	override public fireTransition(Transition transition, EventToken eventTokenToMove) {
		Preconditions.checkArgument(transition != null)
		Preconditions.checkArgument(eventTokenToMove != null)

		//we only allow one transition to be fired per observed matching event on the stream and thus,
		//each token in a given state is marked as processed once a transition is fired,
		//but this might be overridden in other strategies
		eventTokenToMove.currentState.eventTokens.filter[et|et.lastProcessed == null].forEach [ eventToken |
			eventToken.addProcessedEvent(eventModelManager.model.latestEvent)
		]

		val preState = transition.preState
		if (preState instanceof FinalState) {
			return
		}

		val nextState = transition.postState

		eventTokenToMove.setCurrentState(nextState)
		eventModelManager.callbackOnFiredToken(transition, eventTokenToMove)
	}

	def protected addProcessedEvent(EventToken eventToken, Event event) {
		eventToken.recordedEvents.add(event)
		eventToken.lastProcessed = event
	}

	override public handleInitTokenCreation(InternalModel model, AutomatonFactory factory) {

		model.automata.forEach [ automaton |
			if (automaton.initState.empty) {
				newEventToken(automaton, automaton.initState)
			}
		]
	}
}
