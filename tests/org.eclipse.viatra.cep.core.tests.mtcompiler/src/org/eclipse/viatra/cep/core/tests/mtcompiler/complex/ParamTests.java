package org.eclipse.viatra.cep.core.tests.mtcompiler.complex;

import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noEpsilonTransitions;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanStates;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanTransitions;

import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.CepFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.A_1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.B_1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.ParamsAnd_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.ParamsFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.ParamsOr_Pattern;
import org.junit.Assert;
import org.junit.Test;

public class ParamTests extends ComplexTest {

    @Test
    public void followsParamTest() {
        ParamsFollows_Pattern pattern = CepFactory.getInstance().createParamsFollows_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());

        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());
        Transition nextTransition = automaton.getInitialState().getOutTransitions().get(0);
        assertTransitionTypedWith(nextTransition, A_1_Pattern.class);
        assertParameterizedTransition(nextTransition, "param");

        State nextState = nextTransition.getPostState();

        Assert.assertEquals(1, nextState.getOutTransitions().size());
        Transition finalTransition = nextState.getOutTransitions().get(0);
        assertTransitionTypedWith(finalTransition, B_1_Pattern.class);
        assertParameterizedTransition(finalTransition, "param");

        Assert.assertTrue(finalTransition.getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void orParamTest() {
        ParamsOr_Pattern pattern = CepFactory.getInstance().createParamsOr_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        int aTransition = 0;
        int bTransition = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            Assert.assertTrue(transition.getPostState().equals(automaton.getFinalStates().get(0)));
            assertParameterizedTransition(transition, "param");
            if (transitionTypedWith(transition, A_1_Pattern.class)) {
                aTransition++;
            } else if (transitionTypedWith(transition, B_1_Pattern.class)) {
                bTransition++;
            }
        }

        Assert.assertEquals(1, aTransition);
        Assert.assertEquals(1, bTransition);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void andParamTest() {
        ParamsAnd_Pattern pattern = CepFactory.getInstance().createParamsAnd_Pattern();
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
            assertParameterizedTransition(transition, "param");
            if (transitionTypedWith(transition, A_1_Pattern.class)) {
                aTransition++;
                nextStateAfterA = transition.getPostState();
            } else if (transitionTypedWith(transition, B_1_Pattern.class)) {
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
        assertTransitionTypedWith(finalTransition1, B_1_Pattern.class);
        assertParameterizedTransition(finalTransition1, "param");

        Assert.assertEquals(1, nextStateAfterB.getOutTransitions().size());
        Transition finalTransition2 = nextStateAfterB.getOutTransitions().get(0);
        Assert.assertTrue(finalTransition2.getPostState().equals(automaton.getFinalStates().get(0)));
        assertTransitionTypedWith(finalTransition2, A_1_Pattern.class);
        assertParameterizedTransition(finalTransition2, "param");

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

}
