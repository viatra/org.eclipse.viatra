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
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.A_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.B_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Duplicate2_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Duplicate_Pattern;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.junit.Assert;
import org.junit.Test;

public class DuplicateTests extends ComplexTest {

    @Test
    public void duplicateTest() {
        Duplicate_Pattern pattern = CepFactory.getInstance().createDuplicate_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(5, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        Transition transition1 = automaton.getInitialState().getOutTransitions().get(0);
        assertTransitionTypedWith(transition1, A_Pattern.class);
        State nextState = transition1.getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        Transition transition2 = nextState.getOutTransitions().get(0);
        assertTransitionTypedWith(transition2, B_Pattern.class);
        State nextState2 = transition2.getPostState();
        Assert.assertEquals(1, nextState2.getOutTransitions().size());

        Transition transition3 = nextState2.getOutTransitions().get(0);
        assertTransitionTypedWith(transition3, B_Pattern.class);
        Assert.assertTrue(transition3.getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void duplicate2Test() {
        Duplicate2_Pattern pattern = CepFactory.getInstance().createDuplicate2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(3, automaton.getInitialState().getOutTransitions().size());

        int aTransition = 0;
        int bTransition = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            Assert.assertTrue(transition.getPostState().equals(automaton.getFinalStates().get(0)));
            if (transitionTypedWith(transition, A_Pattern.class)) {
                aTransition++;
            } else if (transitionTypedWith(transition, B_Pattern.class)) {
                bTransition++;
            }
        }

        Assert.assertEquals(1, aTransition);
        Assert.assertEquals(2, bTransition);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }
}
