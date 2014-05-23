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

package org.eclipse.viatra.cep.core.engine.compiler;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.Guard;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS;
import org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator;
import org.eclipse.viatra.cep.core.metamodels.events.OR;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Compiler {
    private final AutomatonFactory FACTORY = AutomatonFactory.eINSTANCE;
    private InternalModel model;
    private Automaton automaton;
    private InitState initState;
    private FinalState finalState;

    public Compiler(InternalModel model) {
        this.model = model;
    }

    public Automaton compile(EventPattern eventPattern) {
        Precompiler precompiler = new Precompiler();
        EventPattern unfoldedEventPattern = precompiler.unfoldEventPattern(eventPattern);

        model.eResource().getContents().add(unfoldedEventPattern);

        automaton = FACTORY.createAutomaton();
        automaton.getStates().add(FACTORY.createTrapState());

        initState = FACTORY.createInitState();
        automaton.getStates().add(initState);

        finalState = createFinalState();
        automaton.getStates().add(finalState);

        if (unfoldedEventPattern instanceof ComplexEventPattern) {
            Map<StateType, State> marginStates = map(initState, (ComplexEventPattern) unfoldedEventPattern);

            State lastState = marginStates.get(StateType.OUT);
            List<Transition> inTransitions = lastState.getInTransitions();
            finalState.getInTransitions().addAll(inTransitions);

            List<Transition> outTransitions = lastState.getOutTransitions();
            finalState.getOutTransitions().addAll(outTransitions);

            automaton.getStates().remove(lastState);
        } else if (unfoldedEventPattern instanceof AtomicEventPattern) {
            map((AtomicEventPattern) unfoldedEventPattern);
        }

        automaton.setEventPattern(unfoldedEventPattern);
        model.getAutomata().add(automaton);

        return automaton;
    }

    enum StateType {
        IN, OUT
    }

    private void map(AtomicEventPattern atomicEventPattern) {
        Guard guard = createGuard(atomicEventPattern);
        createTransition(initState, finalState, guard);
    }

    private Map<StateType, State> map(State preState, ComplexEventPattern complexEventPattern) {
        LogicalOperator operator = complexEventPattern.getOperator();
        if (operator instanceof FOLLOWS) {
            return mapFollows(preState, complexEventPattern.getCompositionEvents());
        } else if (operator instanceof OR) {
            return mapOr(preState, complexEventPattern.getCompositionEvents());
        }
        throw new UnsupportedOperationException();
    }

    private Map<StateType, State> mapFollows(State preState, List<EventPattern> compositionEventPatterns) {
        Map<StateType, State> states = Maps.newHashMap();

        states.put(StateType.IN, preState);

        State lastCreatedState = preState;

        for (EventPattern eventPattern : compositionEventPatterns) {
            if (eventPattern instanceof AtomicEventPattern) {
                State currentState = createState();
                Guard guard = createGuard((AtomicEventPattern) eventPattern);
                createTransition(lastCreatedState, currentState, guard);
                lastCreatedState = currentState;
            } else if (eventPattern instanceof ComplexEventPattern) {
                Map<StateType, State> marginStates = map(lastCreatedState, (ComplexEventPattern) eventPattern);
                lastCreatedState = marginStates.get(StateType.OUT);
            }
        }

        states.put(StateType.OUT, lastCreatedState);

        return states;
    }

    private Map<StateType, State> mapOr(State preState, List<EventPattern> compositionEventPatterns) {
        Map<StateType, State> states = Maps.newHashMap();

        states.put(StateType.IN, preState);

        State outState = FACTORY.createState();
        states.put(StateType.OUT, outState);

        List<State> statesToBeMergedIntoOut = Lists.newArrayList();

        for (EventPattern eventPattern : compositionEventPatterns) {
            if (eventPattern instanceof AtomicEventPattern) {
                Guard guard = createGuard((AtomicEventPattern) eventPattern);
                createTransition(preState, outState, guard);
            } else if (eventPattern instanceof ComplexEventPattern) {
                Map<StateType, State> marginStates = map(preState, (ComplexEventPattern) eventPattern);
                statesToBeMergedIntoOut.add(marginStates.get(StateType.OUT));
            }
        }

        for (State state : statesToBeMergedIntoOut) {
            List<Transition> inTransitions = state.getInTransitions();
            outState.getInTransitions().addAll(inTransitions);

            List<Transition> outTransitions = state.getOutTransitions();
            outState.getOutTransitions().addAll(outTransitions);

            automaton.getStates().remove(state);
        }

        return states;
    }

    private State createState() {
        State state = FACTORY.createState();
        automaton.getStates().add(state);
        return state;
    }

    private TypedTransition createTransition(State preState, State postState, Guard guard) {
        TypedTransition transition = FACTORY.createTypedTransition();
        transition.setPreState(preState);
        transition.setPostState(postState);
        transition.setGuard(guard);
        return transition;
    }

    private Guard createGuard(AtomicEventPattern eventType) {
        Guard guard = FACTORY.createGuard();
        guard.setEventType(eventType);
        return guard;
    }

    private FinalState createFinalState() {
        FinalState finalState = FACTORY.createFinalState();
        finalState.setLabel("final");
        return finalState;
    }

    public InitState getInitState() {
        return initState;
    }

    public FinalState getFinalState() {
        return finalState;
    }
}
