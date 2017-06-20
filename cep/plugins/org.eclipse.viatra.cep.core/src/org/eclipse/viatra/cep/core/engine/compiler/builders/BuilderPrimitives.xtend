/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.engine.compiler.builders

import com.google.common.base.Preconditions
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
import org.eclipse.viatra.cep.core.metamodels.automaton.Guard
import org.eclipse.viatra.cep.core.metamodels.automaton.NegativeTransition
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition
import org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference
import org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS
import org.eclipse.viatra.cep.core.metamodels.events.Infinite
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class BuilderPrimitives {
    protected val extension AutomatonFactory automatonFactory = AutomatonFactory.eINSTANCE
    protected val extension TraceFactory traceFactory = TraceFactory.eINSTANCE

    private TraceModel traceModel

    new(TraceModel traceModel) {
        this.traceModel = traceModel
    }

    /**
     * Creates a new {@link State}, connects it with the preState and applies a {@link Guard} with the type of the
     * {@link EventPatternReference}.
     */
    def transitionToNewState(Automaton automaton, EventPatternReference eventPatternReference, State preState) {
        var nextState = createState
        automaton.states += nextState

        transitionBetween(eventPatternReference, preState, nextState)

        nextState
    }

    /**
     * Creates a new {@link State}, connects it with the preState via a {@link NegativeTransition} and applies a
     * {@link Guard} with the type of the {@link EventPatternReference}.
     */
    def negTransitionToNewState(Automaton automaton, EventPatternReference eventPatternReference, State preState) {
        var nextState = createState
        automaton.states += nextState

        negTransitionBetween(eventPatternReference, preState, nextState)

        nextState
    }

    /**
     * Connects the preState and the postState and applies a {@link Guard} with the type of the
     * {@link EventPatternReference}.
     */
    def transitionBetween(EventPatternReference eventPatternReference, State preState, State postState) {
        val transition = newTransition(preState, postState)
        transition.addGuard(eventPatternReference)

        eventPatternReference.handleTransitionParameters(transition)
    }

    /**
     * Connects the preState and the postState and applies a {@link Guard} with the type of the
     * {@link EventPatternReference}.
     */
    def transitionBetween(EventPattern eventPattern, State preState, State postState) {
        val transition = newTransition(preState, postState)
        transition.addGuard(eventPattern)
    }

    /**
     * Connects the preState and the postState with a {@link NegativeTransition} and applies a {@link Guard}
     * with the type of the {@link EventPatternReference}.
     */
    def negTransitionBetween(EventPatternReference eventPatternReference, State preState, State postState) {
        val transition = newNegTransition(preState, postState)
        transition.addGuard(eventPatternReference)

        eventPatternReference.handleTransitionParameters(transition)
    }

    /**
     * Primitive for creating a new {@link Transition}.
     */
    def newTransition(State preState, State postState) {
        val transition = createTypedTransition

        transition.preState = preState
        transition.postState = postState

        transition
    }

    /**
     * Primitive for creating a new {@link NegativeTransition}.
     */
    def newNegTransition(State preState, State postState) {
        val transition = createNegativeTransition

        transition.preState = preState
        transition.postState = postState

        transition
    }

    /**
     * Applies a {@link Guard} with the type of the {@link EventPatternReference} to the {@link Transition}. 
     */
    def addGuard(TypedTransition transition, EventPatternReference eventPatternReference) {
        transition.addGuard(eventPatternReference.eventPattern)
    }

    /**
     * Applies a {@link Guard} with the type of the {@link EventPattern} to the {@link Transition}. 
     */
    def addGuard(TypedTransition transition, EventPattern eventPattern) {
        var guard = createGuard
        guard.eventType = eventPattern
        transition.guards += guard
    }

    /**
     * Maps the an {@link EventPatternReference} wrt its multiplicity between the preState and a fixed postState.
     * Creates an {@link EpsilonTransition} between the last created intermediate state and the postState.
     */
    def mapWithMultiplicity(EventPatternReference eventPatternReference, Automaton automaton, State preState,
        State postState) {
        val lastState = mapWithMultiplicity(eventPatternReference, automaton, preState)

        if (lastState === null) {
            createEpsilon(preState, postState)
        } else {
            createEpsilon(lastState, postState)
        }
    }

    /**
     *  Maps the eventPatternReference wrt its multiplicity without a fixed postState.
     */
    def mapWithMultiplicity(EventPatternReference eventPatternReference, Automaton automaton, State preState) {
        var State nextState = null
        if (eventPatternReference.multiplicity === null) {
            if (nextState === null) {
                nextState = transitionToNewState(automaton, eventPatternReference, preState)
            } else {
                nextState = transitionToNewState(automaton, eventPatternReference, nextState)
            }
        } else if (eventPatternReference.multiplicity instanceof Multiplicity) {
            for (var i = 0; i < (eventPatternReference.multiplicity as Multiplicity).value; i++) {
                if (nextState === null) {
                    nextState = transitionToNewState(automaton, eventPatternReference, preState)
                } else {
                    nextState = transitionToNewState(automaton, eventPatternReference, nextState)
                }
            }
        } else if (eventPatternReference.multiplicity instanceof AtLeastOne ||
            eventPatternReference.multiplicity instanceof Infinite) {

            if (eventPatternReference.multiplicity instanceof AtLeastOne) {
                if (nextState === null) {
                    nextState = transitionToNewState(automaton, eventPatternReference, preState)
                } else {
                    nextState = transitionToNewState(automaton, eventPatternReference, nextState)
                }
            }
            if (nextState === null) {
                nextState = selfTransition(eventPatternReference, preState)
            } else {
                nextState = selfTransition(eventPatternReference, nextState)
            }

        } else {
            throw new IllegalArgumentException
        }
        nextState
    }

    /**
     * Creates a self-transition on the state.
     */
    def selfTransition(EventPatternReference eventPatternReference, State state) {
        var selfTransition = createTypedTransition
        var selfGuard = createGuard
        selfGuard.eventType = eventPatternReference.eventPattern
        selfTransition.guards.add(selfGuard)

        selfTransition.preState = state
        selfTransition.postState = state

        state
    }

    /**
     * Creates an Epsilon-transition between two states.
     */
    def createEpsilon(State preState, State postState) {
        Preconditions::checkArgument(preState !== null && postState !== null)
        var epsilon = createEpsilonTransition
        epsilon.preState = preState
        epsilon.postState = postState
    }

    /**
     * Removes a transition from the model.
     */
    def removeTransition(Transition transition) {
        transition.postState = null
        transition.preState.outTransitions.remove(transition)
    }

    /**
     * Removes a State from the model.
     */
    def removeState(State state) {
        (state.eContainer as Automaton).states.remove(state)
    }

    /**
     * Maps parameters onto a transition.
     */
    def handleTransitionParameters(EventPatternReference eventPatternReference, TypedTransition transition) {
        val parameterSymbolicNames = eventPatternReference.parameterSymbolicNames

        for (var i = 0; i < parameterSymbolicNames.size; i++) {
            val symbolicName = parameterSymbolicNames.get(i)

            if (!(symbolicName.equalsIgnoreCase(TransformationBasedCompiler.OMITTED_PARAMETER_SYMBOLIC_NAME))) {
                var transitionParameter = createParameter
                transitionParameter.symbolicName = symbolicName
                transitionParameter.position = i
                transition.parameters += transitionParameter
            }
        }
    }

    def alignTimewindow(Automaton automaton, ComplexEventPattern eventPattern, TypedTransition transition) {
        val timedZoneTrace = traceModel.timedZoneTraces.findFirst [ tzTrace |
            tzTrace.transition.equals(transition)
        ]
        if (timedZoneTrace === null) {
            return
        }

        transition.preState.outTransitions.filter[tr|!tr.equals(transition)].forEach [ tr |
            Preconditions::checkArgument(tr.postState.outTransitions.size == 1)

            // copy timed zone for every branch
            var timedZone = createWithin
            timedZone.time = timedZoneTrace.timedZone.time
            timedZone.inState = transition.preState
            timedZone.outState = transition.postState
            automaton.timedZones += timedZone

            val tzTrace = createTimedZoneTrace
            tzTrace.timedZone = timedZone
            tzTrace.transition = tr
            traceModel.timedZoneTraces += tzTrace

            // if the branch does not continue with an EpsilonTransition, it's a real branch, thus: align zone
            if (!(tr.postState.outTransitions.head instanceof EpsilonTransition)) {
                tr.postState.inStateOf += timedZone
            }
        ]

        // finally, remove original timed zone and its trace
        automaton.timedZones.remove(timedZoneTrace.timedZone)
        traceModel.timedZoneTraces.remove(timedZoneTrace)
    }

    def alignTimewindow(Automaton automaton, ComplexEventPattern eventPattern, TypedTransition transition,
        State newlyCreatedState) {
        val timedZoneTrace = traceModel.timedZoneTraces.findFirst [ tzTrace |
            tzTrace.transition.equals(transition)
        ]

        if (timedZoneTrace !== null) {
            newlyCreatedState.inStateOf += timedZoneTrace.timedZone
            traceModel.timedZoneTraces.remove(timedZoneTrace)
        }
    }

    def initializeTimewindow(Automaton automaton, EventPattern eventPattern, State inState, State outState) {
        if (!(eventPattern instanceof ComplexEventPattern)) {
            return
        }

        if ((eventPattern as ComplexEventPattern).timewindow === null) {
            return
        }

        Preconditions::checkArgument((eventPattern as ComplexEventPattern).operator instanceof FOLLOWS)

        var timedZone = createWithin
        timedZone.time = (eventPattern as ComplexEventPattern).timewindow.time
        timedZone.inState = inState
        timedZone.outState = outState
        automaton.timedZones += timedZone

        val tzTrace = createTimedZoneTrace
        tzTrace.timedZone = timedZone
        tzTrace.transition = inState.outTransitions.head
        traceModel.timedZoneTraces += tzTrace
    }
}