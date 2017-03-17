/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.core.tests.compiler.complex;

import static org.eclipse.viatra.cep.core.tests.compiler.complex.Utils.noEpsilonTransitions;
import static org.eclipse.viatra.cep.core.tests.compiler.complex.Utils.noOrphanStates;
import static org.eclipse.viatra.cep.core.tests.compiler.complex.Utils.noOrphanTransitions;

import org.eclipse.viatra.cep.core.compiler.testdata.patterns.CepFactory;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.A_1_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.A_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.B_1_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.B_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotAndParams_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotAnd_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotAtomic_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotFollows2_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotFollows3_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotFollowsParams_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotFollows_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotOrParams_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.NotOr_Pattern;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.NegativeTransition;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

public class NotTests extends ComplexTest {

    @Test
    public void notAtomicTest() {
        NotAtomic_Pattern pattern = CepFactory.getInstance().createNotAtomic_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        Transition transition = automaton.getInitialState().getOutTransitions().get(0);
        Assert.assertTrue(transition instanceof NegativeTransition);

        Assert.assertEquals(automaton.getFinalStates().get(0), transition.getPostState());

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void notFollowsTest() {
        NotFollows_Pattern pattern = CepFactory.getInstance().createNotFollows_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        Transition transition1Branch = automaton.getInitialState().getOutTransitions().get(0);
        Assert.assertTrue(transition1Branch instanceof NegativeTransition);
        Assert.assertEquals(automaton.getFinalStates().get(0), transition1Branch.getPostState());

        Transition transition2Branch = automaton.getInitialState().getOutTransitions().get(1);
        Assert.assertTrue(!(transition2Branch instanceof NegativeTransition));
        State postState = transition2Branch.getPostState();
        Assert.assertEquals(1, postState.getOutTransitions().size());

        Transition transition2Branch2 = postState.getOutTransitions().get(0);
        Assert.assertTrue(transition2Branch2 instanceof NegativeTransition);

        Assert.assertEquals(automaton.getFinalStates().get(0), transition2Branch2.getPostState());

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void notFollows2Test() {
        NotFollows2_Pattern pattern = CepFactory.getInstance().createNotFollows2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(5, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        Transition neg1Transition = null;
        Transition pos1Transition = null;

        Transition transition1 = automaton.getInitialState().getOutTransitions().get(0);
        if (transition1 instanceof NegativeTransition) {
            neg1Transition = transition1;
            pos1Transition = automaton.getInitialState().getOutTransitions().get(1);
        } else {
            pos1Transition = transition1;
            neg1Transition = automaton.getInitialState().getOutTransitions().get(1);
        }

        Assert.assertNotNull(neg1Transition);
        Assert.assertNotNull(pos1Transition);

        Assert.assertEquals(automaton.getFinalStates().get(0), neg1Transition.getPostState());

        State nextState1 = pos1Transition.getPostState();
        Assert.assertEquals(2, nextState1.getOutTransitions().size());

        Transition neg11Transition = null;
        Transition pos11Transition = null;

        Transition transition11 = nextState1.getOutTransitions().get(0);
        if (transition11 instanceof NegativeTransition) {
            neg11Transition = transition11;
            pos11Transition = nextState1.getOutTransitions().get(1);
        } else {
            pos11Transition = transition11;
            neg11Transition = nextState1.getOutTransitions().get(1);
        }

        Assert.assertNotNull(neg11Transition);
        Assert.assertNotNull(pos11Transition);

        Assert.assertEquals(automaton.getFinalStates().get(0), neg11Transition.getPostState());

        State nextState2 = pos11Transition.getPostState();
        Assert.assertEquals(1, nextState2.getOutTransitions().size());
        Assert.assertTrue(nextState2.getOutTransitions().get(0) instanceof NegativeTransition);
        Assert.assertEquals(automaton.getFinalStates().get(0), nextState2.getOutTransitions().get(0).getPostState());

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void notFollows3Test() {
        NotFollows3_Pattern pattern = CepFactory.getInstance().createNotFollows3_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(5, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        Transition transition1 = automaton.getInitialState().getOutTransitions().get(0);
        Assert.assertTrue(!(transition1 instanceof NegativeTransition));
        Assert.assertTrue(transitionTypedWith(transition1, A_Pattern.class));

        State nextState1 = transition1.getPostState();
        Assert.assertEquals(1, nextState1.getOutTransitions().size());

        Transition transition2 = nextState1.getOutTransitions().get(0);
        Assert.assertTrue(transition2 instanceof NegativeTransition);
        Assert.assertTrue(transitionTypedWith(transition2, B_Pattern.class));

        State nextState2 = transition2.getPostState();
        Assert.assertEquals(1, nextState2.getOutTransitions().size());

        Transition transition3 = nextState2.getOutTransitions().get(0);
        Assert.assertTrue(!(transition3 instanceof NegativeTransition));
        Assert.assertTrue(transitionTypedWith(transition3, C_Pattern.class));

        Assert.assertEquals(automaton.getFinalStates().get(0), transition3.getPostState());

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void notOrTest() {
        NotOr_Pattern pattern = CepFactory.getInstance().createNotOr_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        Transition transition = automaton.getInitialState().getOutTransitions().get(0);
        Assert.assertTrue(transition instanceof NegativeTransition);
        Assert.assertEquals(automaton.getFinalStates().get(0), transition.getPostState());

        Assert.assertTrue(((NegativeTransition) transition).getGuards().size() == 2);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void notFollowsParamsTest() {
        NotFollowsParams_Pattern pattern = CepFactory.getInstance().createNotFollowsParams_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        int posTransition = 0;
        int negTransition = 0;
        State posFollowupState = null;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transition instanceof NegativeTransition) {
                assertTransitionTypedWith(transition, A_1_Pattern.class);
                assertParameterizedTransition(transition, "param");
                Assert.assertTrue(transition.getPostState().equals(automaton.getFinalStates().get(0)));
                negTransition++;
            } else {
                assertTransitionTypedWith(transition, A_1_Pattern.class);
                assertParameterizedTransition(transition, "param");
                posTransition++;
                posFollowupState = transition.getPostState();
            }
        }

        Assert.assertEquals(1, posTransition);
        Assert.assertEquals(1, negTransition);

        Assert.assertEquals(1, posFollowupState.getOutTransitions().size());

        Transition finalTransition = posFollowupState.getOutTransitions().get(0);
        Assert.assertTrue(finalTransition instanceof NegativeTransition);
        assertTransitionTypedWith(finalTransition, B_1_Pattern.class);
        assertParameterizedTransition(finalTransition, "param");
        Assert.assertTrue(finalTransition.getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Ignore
    @Test
    public void notOrParamsTest() {
        NotOrParams_Pattern pattern = CepFactory.getInstance().createNotOrParams_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        Transition transition = automaton.getInitialState().getOutTransitions().get(0);

        Assert.assertTrue(transition instanceof NegativeTransition);
        Assert.assertTrue(((NegativeTransition) transition).getGuards().size() == 2);
        assertTransitionTypedWith(transition, Lists.newArrayList(A_1_Pattern.class, B_1_Pattern.class));
        assertParameterizedTransition(transition, "param");

        Assert.assertEquals(automaton.getFinalStates().get(0), transition.getPostState());

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Ignore
    @Test
    public void notAndTest() {
        NotAnd_Pattern pattern = CepFactory.getInstance().createNotAnd_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

    }

    @Ignore
    @Test
    public void notAndParamsTest() {
        NotAndParams_Pattern pattern = CepFactory.getInstance().createNotAndParams_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

    }
}
