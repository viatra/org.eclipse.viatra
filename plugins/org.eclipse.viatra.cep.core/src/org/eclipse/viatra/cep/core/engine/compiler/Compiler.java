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

import org.apache.log4j.Logger;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.Guard;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.metamodels.automaton.Within;
import org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne;
import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS;
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.OR;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Compiler functionality that maps the {@link EventPattern}s, combined via complex event operators to the internal
 * representation language of the engine. That is, for every {@link EventPattern}, an {@link Automaton} is created.
 * 
 * @author Istvan David
 * 
 */
public class Compiler {
    private final static Logger LOGGER = LoggerUtils.getInstance().getLogger();
    private final static AutomatonFactory FACTORY = AutomatonFactory.eINSTANCE;

    private InternalModel model;
    private Automaton automaton;
    private InitState initState;
    private FinalState finalState;

    public Compiler(InternalModel model) {
        this.model = model;
    }

    /**
     * The entry point of compiling a single {@link EventPattern}. Based on its type, the specific mapping methods are
     * invoked.
     * 
     * @param eventPattern
     *            the {@link EventPattern} to be compiled into an {@link Automaton}
     * @return the {@link Automaton} created from the {@link EventPattern}
     */
    public Automaton compile(EventPattern eventPattern) {
        LOGGER.debug(String.format("Compiler: Compiling event pattern %s", eventPattern));
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

            Timewindow timewindow = ((ComplexEventPattern) unfoldedEventPattern).getTimewindow();
            if (timewindow != null) {
                createTimedZone(timewindow, initState, finalState);
            }

        } else if (unfoldedEventPattern instanceof AtomicEventPattern) {
            map((AtomicEventPattern) unfoldedEventPattern);
        }

        automaton.setEventPattern(unfoldedEventPattern);
        model.getAutomata().add(automaton);

        return automaton;
    }

    /**
     * Designates whether the {@link State} is the input or the output state of a sub-automaton.
     * 
     * @author Istvan David
     * 
     */
    enum StateType {
        IN, OUT
    }

    private void map(AtomicEventPattern atomicEventPattern) {
        Guard guard = createGuard(atomicEventPattern);
        createTransition(initState, finalState, guard);
    }

    /**
     * Dispatch method for general {@link ComplexEventPattern}s. Forwards the pattern to the appropriate map method that
     * deals with the particular type of {@link ComplexEventOperator}.
     * 
     * @param preState
     *            the {@link State} the automaton begins with
     * @param complexEventPattern
     *            the single {@link ComplexEventPattern} that an {@link Automaton} should be created from
     * @return the IN and OUT ({@link StateType}s) {@link State} of the automaton
     */
    private Map<StateType, State> map(State preState, ComplexEventPattern complexEventPattern) {
        ComplexEventOperator operator = complexEventPattern.getOperator();
        if (operator instanceof FOLLOWS) {
            return mapFollows(preState, complexEventPattern);
        } else if (operator instanceof OR) {
            return mapOr(preState, complexEventPattern);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Maps a single non-nested {@link EventPattern} featuring a {@link FOLLOWS} operator onto an {@link Automaton}.
     * 
     * @param preState
     *            the {@link State} the automaton begins with
     * @param complexEventPattern
     *            {@link ComplexEventPattern} to be mapped
     * @return the IN and OUT ({@link StateType}s) {@link State} of the automaton
     */
    private Map<StateType, State> mapFollows(State preState, ComplexEventPattern complexEventPattern) {
        Map<StateType, State> marginStates = Maps.newHashMap();

        marginStates.put(StateType.IN, preState);

        State lastCreatedState = preState;

        for (EventPatternReference eventPatternReference : complexEventPattern.getContainedEventPatterns()) {
            EventPattern eventPattern = eventPatternReference.getEventPattern();
            AbstractMultiplicity multiplicity = eventPatternReference.getMultiplicity();
            if (multiplicity instanceof Multiplicity) {
                for (int i = 0; i < ((Multiplicity) multiplicity).getValue(); i++) {
                    Map<StateType, State> subPathStates = mapFollowsPath(lastCreatedState, eventPattern);
                    lastCreatedState = subPathStates.get(StateType.OUT);
                }
            } else if (multiplicity instanceof AtLeastOne) {
                Map<StateType, State> subPathStates = mapFollowsPath(lastCreatedState, eventPattern);
                lastCreatedState = subPathStates.get(StateType.OUT);

                for (Transition transition : marginStates.get(StateType.IN).getOutTransitions()) {
                    if (!(transition instanceof TypedTransition)) {
                        continue;
                    }
                    Guard guard = ((TypedTransition) transition).getGuard();
                    Guard backwardLoopGuard = createGuard(guard.getEventType());
                    createTransition(lastCreatedState, transition.getPostState(), backwardLoopGuard);
                }
            } else {
                throw new UnsupportedOperationException(); // TODO Infinite case should be implemented here
            }

        }

        marginStates.put(StateType.OUT, lastCreatedState);

        return marginStates;
    }

    private Map<StateType, State> mapFollowsPath(State preState, EventPattern eventPattern) {
        Map<StateType, State> marginStates = Maps.newHashMap();
        if (eventPattern instanceof AtomicEventPattern) {
            State currentState = createState();
            Guard guard = createGuard((AtomicEventPattern) eventPattern);
            createTransition(preState, currentState, guard);
            marginStates.put(StateType.IN, preState);
            marginStates.put(StateType.OUT, currentState);
        } else if (eventPattern instanceof ComplexEventPattern) {
            marginStates = map(preState, (ComplexEventPattern) eventPattern);
        }
        return marginStates;
    }

    /**
     * Maps a single non-nested {@link EventPattern} featuring an {@link OR} operator onto an {@link Automaton}.
     * 
     * @param preState
     *            the {@link State} the automaton begins with
     * @param complexEventPattern
     *            {@link ComplexEventPattern} to be mapped
     * @return the IN and OUT ({@link StateType}s) {@link State} of the automaton
     */
    private Map<StateType, State> mapOr(State preState, ComplexEventPattern complexEventPattern) {
        Map<StateType, State> states = Maps.newHashMap();

        states.put(StateType.IN, preState);

        State outState = createState();
        states.put(StateType.OUT, outState);

        List<State> statesToBeMergedIntoOut = Lists.newArrayList();

        for (EventPatternReference eventPatternReference : complexEventPattern.getContainedEventPatterns()) {
            EventPattern eventPattern = eventPatternReference.getEventPattern();
            mapOrPath(preState, outState, statesToBeMergedIntoOut, eventPattern);
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

    private void mapOrPath(State preState, State outState, List<State> statesToBeMergedIntoOut,
            EventPattern eventPattern) {
        if (eventPattern instanceof AtomicEventPattern) {
            Guard guard = createGuard((AtomicEventPattern) eventPattern);
            createTransition(preState, outState, guard);
        } else if (eventPattern instanceof ComplexEventPattern) {
            Map<StateType, State> marginStates = map(preState, (ComplexEventPattern) eventPattern);
            statesToBeMergedIntoOut.add(marginStates.get(StateType.OUT));
        }
    }

    private void createTimedZone(Timewindow timewindow, State inState, State outState) {
        Within timedZone = AutomatonFactory.eINSTANCE.createWithin();
        timedZone.setTime(timewindow.getTime());
        timedZone.setInState(inState);
        timedZone.setOutState(outState);
        automaton.getTimedZones().add(timedZone);
    }

    private void createTimedZone(Timewindow timewindow, InitState initState, State outState) {
        List<Transition> outTransitions = initState.getOutTransitions();
        for (Transition transition : outTransitions) {
            State timeZoneInState = transition.getPostState();
            createTimedZone(timewindow, timeZoneInState, outState);
        }
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
        FinalState state = FACTORY.createFinalState();
        state.setLabel("final");
        return state;
    }

    /**
     * @return the {@link InitState} of the {@link Automaton}
     */
    public InitState getInitState() {
        return initState;
    }

    /**
     * @return the {@link FinalState} of the {@link Automaton}
     */
    public FinalState getFinalState() {
        return finalState;
    }
}
