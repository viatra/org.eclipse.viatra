package org.eclipse.viatra.cep.core.tests.mtcompiler.complex;

import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noEpsilonTransitions;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanStates;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanTransitions;

import org.eclipse.viatra.cep.core.experimental.mtcompiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.CepFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.A_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.B_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.And2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.And_Pattern;
import org.junit.Assert;
import org.junit.Test;

public class AndTests extends ComplexTest {

    @Test
    public void andTest() {
        And_Pattern pattern = CepFactory.getInstance().createAnd_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(5, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        int aTransition = 0;
        State nextStateAfterA = null;
        int bTransition = 0;
        State nextStateAfterB = null;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transitionTypedWith(transition, A_Pattern.class)) {
                aTransition++;
                nextStateAfterA = transition.getPostState();
            } else if (transitionTypedWith(transition, B_Pattern.class)) {
                bTransition++;
                nextStateAfterB = transition.getPostState();
            }
        }

        Assert.assertEquals(1, aTransition);
        Assert.assertNotNull(nextStateAfterA);
        Assert.assertEquals(1, bTransition);
        Assert.assertNotNull(nextStateAfterB);

        Assert.assertEquals(1, nextStateAfterA.getOutTransitions().size());
        Transition finalTransition1 = nextStateAfterA.getOutTransitions().get(0);
        Assert.assertTrue(finalTransition1.getPostState().equals(automaton.getFinalStates().get(0)));
        assertTransitionTypedWith(finalTransition1, B_Pattern.class);

        Assert.assertEquals(1, nextStateAfterB.getOutTransitions().size());
        Transition finalTransition2 = nextStateAfterB.getOutTransitions().get(0);
        Assert.assertTrue(finalTransition2.getPostState().equals(automaton.getFinalStates().get(0)));
        assertTransitionTypedWith(finalTransition2, A_Pattern.class);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void and2Test() {
        And2_Pattern pattern = CepFactory.getInstance().createAnd2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(7, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transitionTypedWith(transition, A_Pattern.class)) {
                assertTransitionTypedWith(transition, A_Pattern.class);
                State nextStateAfterA = transition.getPostState();
                Assert.assertEquals(1, nextStateAfterA.getOutTransitions().size());
                Transition transition2 = nextStateAfterA.getOutTransitions().get(0);
                assertTransitionTypedWith(transition2, B_Pattern.class);
                State nextStateAfterB = transition2.getPostState();
                Assert.assertEquals(1, nextStateAfterB.getOutTransitions().size());
                Transition transition3 = nextStateAfterB.getOutTransitions().get(0);
                assertTransitionTypedWith(transition3, C_Pattern.class);
                Assert.assertTrue(transition3.getPostState().equals(automaton.getFinalStates().get(0)));
            } else if (transitionTypedWith(transition, C_Pattern.class)) {
                assertTransitionTypedWith(transition, C_Pattern.class);
                State nextStateAfterC = transition.getPostState();
                Assert.assertEquals(1, nextStateAfterC.getOutTransitions().size());
                Transition transition2 = nextStateAfterC.getOutTransitions().get(0);
                assertTransitionTypedWith(transition2, A_Pattern.class);
                State nextStateAfterA = transition2.getPostState();
                Assert.assertEquals(1, nextStateAfterA.getOutTransitions().size());
                Transition transition3 = nextStateAfterA.getOutTransitions().get(0);
                assertTransitionTypedWith(transition3, B_Pattern.class);
                Assert.assertTrue(transition3.getPostState().equals(automaton.getFinalStates().get(0)));
            }
        }

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

}
