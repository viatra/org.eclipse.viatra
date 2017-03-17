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
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.AtLeast1_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.AtLeast2_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Inf1_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Inf2_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.MultiplicityOnAtomic_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.MultiplicityOnComplex1_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.MultiplicityOnComplex2_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.MultiplicityOnComplex3_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.MultiplicityOnComplex4_Pattern;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.junit.Assert;
import org.junit.Test;

public class MultiplicityTests extends ComplexTest {

    @Test
    public void multiplicityOnAtomicTest() {
        MultiplicityOnAtomic_Pattern pattern = CepFactory.getInstance().createMultiplicityOnAtomic_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(5, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        State nextState2 = nextState.getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState2.getOutTransitions().size());

        Assert.assertTrue(nextState2.getOutTransitions().get(0).getPostState()
                .equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void multiplicityOnComplex1Test() {
        MultiplicityOnComplex1_Pattern pattern = CepFactory.getInstance().createMultiplicityOnComplex1_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(6, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        State nextState2 = nextState.getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState2.getOutTransitions().size());

        State nextState3 = nextState2.getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState3.getOutTransitions().size());

        Assert.assertTrue(nextState3.getOutTransitions().get(0).getPostState()
                .equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void multiplicityOnComplex2Test() {
        MultiplicityOnComplex2_Pattern pattern = CepFactory.getInstance().createMultiplicityOnComplex2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(6, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        State nextState2 = nextState.getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState2.getOutTransitions().size());

        State nextState3 = nextState2.getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState3.getOutTransitions().size());

        Assert.assertTrue(nextState3.getOutTransitions().get(0).getPostState()
                .equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void multiplicityOnComplex3Test() {
        MultiplicityOnComplex3_Pattern pattern = CepFactory.getInstance().createMultiplicityOnComplex3_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        automaton.getInitialState().getOutTransitions().get(0)
                .equals(automaton.getInitialState().getOutTransitions().get(1));

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(2, nextState.getOutTransitions().size());

        nextState.getOutTransitions().get(0).equals(nextState.getOutTransitions().get(1));
        Assert.assertTrue(nextState.getOutTransitions().get(0).getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void multiplicityOnComplex4Test() {
        MultiplicityOnComplex4_Pattern pattern = CepFactory.getInstance().createMultiplicityOnComplex4_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        automaton.getInitialState().getOutTransitions().get(0)
                .equals(automaton.getInitialState().getOutTransitions().get(1));

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(2, nextState.getOutTransitions().size());

        nextState.getOutTransitions().get(0).equals(nextState.getOutTransitions().get(1));
        Assert.assertTrue(nextState.getOutTransitions().get(0).getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void inf1Test() {
        Inf1_Pattern pattern = CepFactory.getInstance().createInf1_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        int selfBranch = 0;
        int forwardBranch = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transition.getPostState().equals(automaton.getInitialState())) {
                selfBranch++;
            } else {
                forwardBranch++;
                Assert.assertTrue(transition.getPostState().equals(automaton.getFinalStates().get(0)));
            }
        }

        Assert.assertEquals(1, selfBranch);
        Assert.assertEquals(1, forwardBranch);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void inf2Test() {
        Inf2_Pattern pattern = CepFactory.getInstance().createInf2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        int selfBranch = 0;
        int forwardBranch = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transition.getPostState().equals(automaton.getFinalStates().get(0))) {
                forwardBranch++;
            } else {
                selfBranch++;
                State nextState = transition.getPostState();
                Assert.assertEquals(1, nextState.getOutTransitions().size());
                Assert.assertTrue(nextState.getOutTransitions().get(0).getPostState()
                        .equals(automaton.getInitialState()));
            }
        }

        Assert.assertEquals(1, selfBranch);
        Assert.assertEquals(1, forwardBranch);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void atLeast1Test() {
        AtLeast1_Pattern pattern = CepFactory.getInstance().createAtLeast1_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(2, nextState.getOutTransitions().size());

        int selfBranch = 0;
        int forwardBranch = 0;

        for (Transition transition : nextState.getOutTransitions()) {
            if (transition.getPostState().equals(automaton.getFinalStates().get(0))) {
                forwardBranch++;
            } else {
                selfBranch++;
                Assert.assertTrue(transition.getPostState().equals(transition.getPreState()));
            }
        }

        Assert.assertEquals(1, selfBranch);
        Assert.assertEquals(1, forwardBranch);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void atLeast2Test() {
        AtLeast2_Pattern pattern = CepFactory.getInstance().createAtLeast2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(6, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        State nextState2 = nextState.getOutTransitions().get(0).getPostState();
        Assert.assertEquals(2, nextState2.getOutTransitions().size());

        int selfBranch = 0;
        int forwardBranch = 0;

        for (Transition transition : nextState2.getOutTransitions()) {
            if (transition.getPostState().equals(automaton.getFinalStates().get(0))) {
                forwardBranch++;
            } else {
                selfBranch++;
                State nextState3 = transition.getPostState();
                Assert.assertEquals(1, nextState3.getOutTransitions().size());
                Assert.assertTrue(nextState3.getOutTransitions().get(0).getPostState().equals(nextState2));
            }
        }

        Assert.assertEquals(1, selfBranch);
        Assert.assertEquals(1, forwardBranch);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }
}
