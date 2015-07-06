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
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.Guard;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.NegativeTransition;
import org.eclipse.viatra.cep.core.metamodels.automaton.Parameter;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone;
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
import org.eclipse.viatra.cep.core.metamodels.events.Infinite;
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.NEG;
import org.eclipse.viatra.cep.core.metamodels.events.OR;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
    private final static String OMITTED_PARAMETER_SYMBOLIC_NAME = "_";

    private InternalModel model;
    private Automaton automaton;
    private InitState initState;
    private FinalState finalState;

    private List<TypedTransition> infiniteTransitions = Lists.newArrayList();

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
            SubAutomaton subAutomatonData = map(initState, (ComplexEventPattern) unfoldedEventPattern);

            State lastState = subAutomatonData.getOutState();
            replaceState(lastState, finalState);
        } else if (unfoldedEventPattern instanceof AtomicEventPattern) {
            map((AtomicEventPattern) unfoldedEventPattern);
        }

        mergeAtLeastOneIntoInfiniteTransition();
        new Minimize(automaton).minimize();

        automaton.setEventPattern(unfoldedEventPattern);
        model.getAutomata().add(automaton);

        return automaton;
    }

    /**
     * Carries information about the sub-automaton parts within an automaton during compilation.
     * 
     * @author Istvan David
     *
     */
    public static class SubAutomaton {
        private List<State> inStates = Lists.newArrayList();
        private State outState;
        private Set<State> states = Sets.newHashSet();

        public SubAutomaton(List<State> inStates, State outState) {
            this.inStates = inStates;
            this.outState = outState;
            states.addAll(inStates);
            states.add(outState);
        }

        public SubAutomaton(List<State> inStates, State outState, List<State> allStates) {
            this.inStates = inStates;
            this.outState = outState;
            this.states.addAll(allStates);
        }

        public List<State> getInStates() {
            return inStates;
        }

        public State getOutState() {
            return outState;
        }

        public Set<State> getStates() {
            return states;
        }
    }

    private void map(AtomicEventPattern atomicEventPattern) {
        Guard guard = createGuard(atomicEventPattern);
        createTransition(initState, finalState, guard); // no parameters in this case
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
    private SubAutomaton map(State preState, ComplexEventPattern complexEventPattern) {
        SubAutomaton subAutomatonData = null;
        ComplexEventOperator operator = complexEventPattern.getOperator();
        if (operator instanceof NEG) {
            subAutomatonData = mapNeg(preState, complexEventPattern);
        } else if (operator instanceof FOLLOWS) {
            subAutomatonData = mapFollows(preState, complexEventPattern);
        } else if (operator instanceof OR) {
            subAutomatonData = mapOr(preState, complexEventPattern);
        }

        // create timewindow
        Timewindow timewindow = complexEventPattern.getTimewindow();
        if (timewindow != null) {
            createTimedZone(timewindow, subAutomatonData.getInStates(), subAutomatonData.getOutState());
        }

        // return
        return subAutomatonData;
    }

    private SubAutomaton mapNeg(State preState, ComplexEventPattern complexEventPattern) {
        Preconditions.checkArgument(complexEventPattern.getContainedEventPatterns().size() == 1);
        EventPattern eventPattern = complexEventPattern.getContainedEventPatterns().get(0).getEventPattern();

        // it should be an atomic event pattern, i.e.
        // ...it's represented as an anonymous complex event pattern, but
        Preconditions.checkArgument(eventPattern instanceof ComplexEventPattern);
        // ...with multiplicity of 1, and
        AbstractMultiplicity multiplicity = complexEventPattern.getContainedEventPatterns().get(0).getMultiplicity();
        Preconditions.checkArgument(multiplicity instanceof Multiplicity);
        Preconditions.checkArgument(((Multiplicity) multiplicity).getValue() == 1);
        // ...with a FOLLOWS operator
        Preconditions.checkArgument(((ComplexEventPattern) eventPattern).getOperator() instanceof FOLLOWS);

        // the actual atomic event pattern
        Preconditions.checkArgument(((ComplexEventPattern) eventPattern).getContainedEventPatterns().size() == 1);
        Preconditions.checkArgument(((ComplexEventPattern) eventPattern).getContainedEventPatterns().get(0)
                .getEventPattern() instanceof AtomicEventPattern);

        AtomicEventPattern atomicEventPattern = (AtomicEventPattern) ((ComplexEventPattern) eventPattern)
                .getContainedEventPatterns().get(0).getEventPattern();
        List<String> parameterSymbolicNames = ((ComplexEventPattern) eventPattern).getContainedEventPatterns().get(0)
                .getParameterSymbolicNames();

        State postState = createState();
        Guard guard = createGuard(atomicEventPattern);

        createNegativeTransition(preState, postState, guard, parameterSymbolicNames);

        return new SubAutomaton(Lists.newArrayList(postState), postState);
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
    private SubAutomaton mapFollows(State preState, ComplexEventPattern complexEventPattern) {
        List<State> inStates = Lists.newArrayList();
        List<State> allStates = Lists.newArrayList();
        State lastCreatedState = preState;

        for (EventPatternReference eventPatternReference : complexEventPattern.getContainedEventPatterns()) {
            EventPattern eventPattern = eventPatternReference.getEventPattern();
            AbstractMultiplicity multiplicity = eventPatternReference.getMultiplicity();
            List<String> parameterSymbolicNames = eventPatternReference.getParameterSymbolicNames();

            if (multiplicity instanceof Multiplicity) {
                for (int i = 0; i < ((Multiplicity) multiplicity).getValue(); i++) {
                    SubAutomaton subAutomatonData = mapFollowsPath(lastCreatedState, eventPattern,
                            parameterSymbolicNames);
                    lastCreatedState = subAutomatonData.getOutState();
                    if (inStates.isEmpty()) {
                        inStates.addAll(subAutomatonData.getInStates());
                    }
                    allStates.addAll(subAutomatonData.getInStates());
                    allStates.add(subAutomatonData.getOutState());
                }
            } else if ((multiplicity instanceof AtLeastOne) || (multiplicity instanceof Infinite)) {
                SubAutomaton subAutomatonData = mapFollowsPath(lastCreatedState, eventPattern, parameterSymbolicNames);
                lastCreatedState = subAutomatonData.getOutState();
                allStates.addAll(subAutomatonData.getInStates());
                allStates.add(subAutomatonData.getOutState());
                for (Transition transition : preState.getOutTransitions()) {
                    if (!(transition instanceof TypedTransition)) {
                        continue;
                    }
                    Guard guard = ((TypedTransition) transition).getGuards().get(0);
                    Guard backwardLoopGuard = createGuard(guard.getEventType());
                    if (transition instanceof NegativeTransition) {
                        createNegativeTransition(lastCreatedState, transition.getPostState(), backwardLoopGuard,
                                parameterSymbolicNames);
                    } else { // TypedTransition
                        createTransition(lastCreatedState, transition.getPostState(), backwardLoopGuard,
                                parameterSymbolicNames);
                    }
                    if (multiplicity instanceof Infinite) {
                        infiniteTransitions.add((TypedTransition) transition);
                    }
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return new SubAutomaton(inStates, lastCreatedState, allStates);
    }

    private void mergeAtLeastOneIntoInfiniteTransition() {
        for (TypedTransition transition : infiniteTransitions) {
            State preState = transition.getPreState();
            State postState = transition.getPostState();

            // Merge
            // ...copy in states
            postState.getInTransitions().addAll(preState.getInTransitions());
            // ...copy out states except the transition itself
            List<Transition> outTransitionsToCopy = preState.getOutTransitions();
            outTransitionsToCopy.remove(transition);
            postState.getOutTransitions().addAll(outTransitionsToCopy);
            // ...copy timed zones
            postState.getInStateOf().addAll(preState.getInStateOf());
            postState.getOutStateOf().addAll(preState.getOutStateOf());

            // ...remove transition
            preState.getOutTransitions().remove(transition);
            // ...delete preState
            automaton.getStates().remove(preState);
        }
    }

    private SubAutomaton mapFollowsPath(State preState, EventPattern eventPattern, List<String> parameterSymbolicNames) {
        if (eventPattern instanceof AtomicEventPattern) {
            State currentState = createState();
            Guard guard = createGuard((AtomicEventPattern) eventPattern);
            createTransition(preState, currentState, guard, parameterSymbolicNames);
            return new SubAutomaton(Lists.newArrayList(currentState), currentState);
        } else if (eventPattern instanceof ComplexEventPattern) {
            return map(preState, (ComplexEventPattern) eventPattern);
        }
        throw new IllegalArgumentException();
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
    private SubAutomaton mapOr(State preState, ComplexEventPattern complexEventPattern) {
        List<State> inStates = Lists.newArrayList();
        List<State> statesToBeMergedIntoOut = Lists.newArrayList();

        State outState = createState();

        for (EventPatternReference eventPatternReference : complexEventPattern.getContainedEventPatterns()) {
            EventPattern eventPattern = eventPatternReference.getEventPattern();
            EList<String> parameterSymbolicNames = eventPatternReference.getParameterSymbolicNames();
            mapOrPath(preState, outState, inStates, statesToBeMergedIntoOut, eventPattern, parameterSymbolicNames);
        }

        for (State state : statesToBeMergedIntoOut) {
            replaceState(state, outState);
        }

        return new SubAutomaton(inStates, outState);
    }

    private void mapOrPath(State preState, State outState, List<State> inStates, List<State> statesToBeMergedIntoOut,
            EventPattern eventPattern, List<String> parameterSymbolicNames) {
        if (eventPattern instanceof AtomicEventPattern) {
            Guard guard = createGuard((AtomicEventPattern) eventPattern);
            createTransition(preState, outState, guard, parameterSymbolicNames);
        } else if (eventPattern instanceof ComplexEventPattern) {
            SubAutomaton subAutomatonData = map(preState, (ComplexEventPattern) eventPattern);
            inStates.addAll(subAutomatonData.getInStates());
            statesToBeMergedIntoOut.add(subAutomatonData.getOutState());
        }
    }

    /**
     * Replaces the oldState with the newState with respect to its incoming and outgoing transitions, and time windows
     * it participates in.
     * 
     * @param oldState
     *            the state to be replaced
     * @param newState
     *            the state replacing the old one
     */
    private void replaceState(State oldState, State newState) {
        List<Transition> inTransitions = oldState.getInTransitions();
        newState.getInTransitions().addAll(inTransitions);

        List<Transition> outTransitions = oldState.getOutTransitions();
        newState.getOutTransitions().addAll(outTransitions);

        List<TimedZone> timedZones = automaton.getTimedZones();
        for (TimedZone timedZone : timedZones) {
            if (timedZone.getOutState().equals(oldState)) {
                timedZone.setOutState(newState);
            }
        }
        for (TimedZone timedZone : timedZones) {
            if (timedZone.getInState().equals(oldState)) {
                timedZone.setInState(newState);
            }
        }

        automaton.getStates().remove(oldState);
    }

    private void createTimedZone(Timewindow timewindow, List<State> inStates, State outState) {
        for (State inState : inStates) {
            createTimedZone(timewindow, inState, outState);
        }
    }

    private void createTimedZone(Timewindow timewindow, State inState, State outState) {
        Within timedZone = AutomatonFactory.eINSTANCE.createWithin();
        timedZone.setTime(timewindow.getTime());
        timedZone.setInState(inState);
        timedZone.setOutState(outState);
        automaton.getTimedZones().add(timedZone);
    }

    private State createState() {
        State state = FACTORY.createState();
        automaton.getStates().add(state);
        return state;
    }

    private TypedTransition createTransition(State preState, State postState, Guard guard) {
        TypedTransition transition = FACTORY.createTypedTransition();
        setTransitionProperties(transition, preState, postState, guard);
        return transition;
    }

    private NegativeTransition createNegativeTransition(State preState, State postState, Guard guard) {
        NegativeTransition transition = FACTORY.createNegativeTransition();
        setTransitionProperties(transition, preState, postState, guard);
        return transition;
    }

    private void setTransitionProperties(TypedTransition transition, State preState, State postState, Guard guard) {
        transition.setPreState(preState);
        transition.setPostState(postState);
        transition.getGuards().add(guard);
    }

    private TypedTransition createTransition(State preState, State postState, Guard guard, List<String> parameters) {
        TypedTransition transition = createTransition(preState, postState, guard);
        setTransitionParameters(transition, parameters);
        return transition;
    }

    private NegativeTransition createNegativeTransition(State preState, State postState, Guard guard,
            List<String> parameters) {
        NegativeTransition transition = createNegativeTransition(preState, postState, guard);
        setTransitionParameters(transition, parameters);
        return transition;
    }

    private void setTransitionParameters(TypedTransition transition, List<String> parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            String symbolicName = parameters.get(i);

            if (symbolicName.equalsIgnoreCase(OMITTED_PARAMETER_SYMBOLIC_NAME)) {
                continue;
            }

            Parameter transitionParameter = FACTORY.createParameter();
            transitionParameter.setSymbolicName(symbolicName);
            transitionParameter.setPosition(i);
            transition.getParameters().add(transitionParameter);
        }
    }

    private Guard createGuard(EventPattern eventType) {
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
