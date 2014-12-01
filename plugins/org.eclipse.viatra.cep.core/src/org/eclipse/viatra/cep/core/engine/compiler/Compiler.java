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
import org.eclipse.emf.ecore.EObject;
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
import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS;
import org.eclipse.viatra.cep.core.metamodels.events.OR;
import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;
import org.eclipse.viatra.cep.core.metamodels.events.UNTIL;

import com.google.common.base.Preconditions;
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

            TimeWindow timeWindow = ((ComplexEventPattern) unfoldedEventPattern).getTimeWindow();
            if (timeWindow != null) {
                createTimedZone(timeWindow, initState, finalState);
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
        } else if (operator instanceof UNTIL) {
            return mapUntil(preState, complexEventPattern);
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
        Map<StateType, State> states = Maps.newHashMap();

        states.put(StateType.IN, preState);

        State lastCreatedState = preState;

        for (EventPatternReference eventPatternReference : complexEventPattern.getContainedEventPatterns()) {
            EventPattern eventPattern = eventPatternReference.getEventPattern();
            // int multiplicity = eventPatternReference.getMultiplicity();
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

        State outState = FACTORY.createState();
        states.put(StateType.OUT, outState);

        List<State> statesToBeMergedIntoOut = Lists.newArrayList();

        for (EventPatternReference eventPatternReference : complexEventPattern.getContainedEventPatterns()) {
            EventPattern eventPattern = eventPatternReference.getEventPattern();
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

    /**
     * Maps a single non-nested {@link EventPattern} featuring an {@link UNTIL} operator onto an {@link Automaton}.
     * 
     * @param preState
     *            the {@link State} the automaton begins with
     * @param complexEventPattern
     *            {@link ComplexEventPattern} to be mapped
     * @return the IN and OUT ({@link StateType}s) {@link State} of the automaton
     */
    private Map<StateType, State> mapUntil(State preState, ComplexEventPattern complexEventPattern) {
        Map<StateType, State> states = Maps.newHashMap();

        states.put(StateType.IN, preState);

        List<EventPatternReference> containedEventPatterns = complexEventPattern.getContainedEventPatterns();
        Preconditions.checkArgument(containedEventPatterns.size() == 2); // UNTIL is a binary operator

        EventPattern selfEdgePattern = containedEventPatterns.get(0).getEventPattern();
        EventPattern advanceEdgePattern = containedEventPatterns.get(1).getEventPattern();

        if (selfEdgePattern instanceof AtomicEventPattern) {
            Guard guard = createGuard((AtomicEventPattern) selfEdgePattern);
            createTransition(preState, preState, guard);
        } else if (selfEdgePattern instanceof ComplexEventPattern) {
            Map<StateType, State> marginStates = map(preState, (ComplexEventPattern) selfEdgePattern);
            State stateToBeReplaced = marginStates.get(StateType.OUT);
            replace(stateToBeReplaced, preState);
        }

        if (advanceEdgePattern instanceof AtomicEventPattern) {
            State currentState = createState();
            Guard guard = createGuard((AtomicEventPattern) advanceEdgePattern);
            createTransition(preState, currentState, guard);
            states.put(StateType.OUT, currentState);
        } else if (selfEdgePattern instanceof ComplexEventPattern) {
            Map<StateType, State> marginStates = map(preState, (ComplexEventPattern) advanceEdgePattern);
            states.put(StateType.OUT, marginStates.get(StateType.OUT));
        }

        return states;
    }

    /**
     * Replaces a {@link State} with another.
     * 
     * @param stateToBeReplaced
     *            the {@link State} to be replaced
     * @param replaceWith
     *            the {@link State} to replace with
     */
    private void replace(State stateToBeReplaced, State replaceWith) {
        Transition[] inTransitions = stateToBeReplaced.getInTransitions().toArray(
                new Transition[stateToBeReplaced.getInTransitions().size()]);
        for (Transition transition : inTransitions) {
            transition.setPostState(replaceWith);
        }

        Transition[] outTransitions = stateToBeReplaced.getOutTransitions().toArray(
                new Transition[stateToBeReplaced.getOutTransitions().size()]);
        for (Transition transition : outTransitions) {
            transition.setPreState(replaceWith);
        }

        replaceWith.setInStateOf(stateToBeReplaced.getInStateOf());
        replaceWith.setOutStateOf(stateToBeReplaced.getOutStateOf());

        EObject eContainer = stateToBeReplaced.eContainer();
        Preconditions.checkArgument(eContainer instanceof Automaton);
        ((Automaton) eContainer).getStates().remove(stateToBeReplaced);
    }

    private void createTimedZone(TimeWindow timeWindow, State inState, State outState) {
        Within timedZone = AutomatonFactory.eINSTANCE.createWithin();
        timedZone.setTime(timeWindow.getTime());
        timedZone.setInState(inState);
        timedZone.setOutState(outState);
        automaton.getTimedZones().add(timedZone);
    }

    private void createTimedZone(TimeWindow timeWindow, InitState initState, State outState) {
        List<Transition> outTransitions = initState.getOutTransitions();
        for (Transition transition : outTransitions) {
            State timeZoneInState = transition.getPostState();
            createTimedZone(timeWindow, timeZoneInState, outState);
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
