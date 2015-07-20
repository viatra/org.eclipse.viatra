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
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedOrWithFollows1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedOrWithFollows2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedOr_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Or_Pattern;
import org.junit.Assert;
import org.junit.Test;

public class BasicStructuralTests extends ComplexTest {

    @Test
    public void followsTest() {
        Follows_Pattern pattern = CepFactory.getInstance().createFollows_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());
        Transition transition1 = automaton.getInitialState().getOutTransitions().get(0);
        assertTransitionTypedWith(transition1, A_Pattern.class);
        State nextState = transition1.getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());
        Transition transition2 = nextState.getOutTransitions().get(0);
        assertTransitionTypedWith(transition2, B_Pattern.class);
        Assert.assertTrue(transition2.getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void nestedFollowsTest() {
        NestedFollows_Pattern pattern = CepFactory.getInstance().createNestedFollows_Pattern();
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
        assertTransitionTypedWith(transition3, C_Pattern.class);
        Assert.assertTrue(transition3.getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void orTest() {
        Or_Pattern pattern = CepFactory.getInstance().createOr_Pattern();
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
            if (transitionTypedWith(transition, A_Pattern.class)) {
                aTransition++;
            } else if (transitionTypedWith(transition, B_Pattern.class)) {
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
    public void nestedOrTest() {
        NestedOr_Pattern pattern = CepFactory.getInstance().createNestedOr_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(3, automaton.getStates().size());
        Assert.assertEquals(3, automaton.getInitialState().getOutTransitions().size());

        int aTransition = 0;
        int bTransition = 0;
        int cTransition = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            Assert.assertTrue(transition.getPostState().equals(automaton.getFinalStates().get(0)));
            if (transitionTypedWith(transition, A_Pattern.class)) {
                aTransition++;
            } else if (transitionTypedWith(transition, B_Pattern.class)) {
                bTransition++;
            } else if (transitionTypedWith(transition, C_Pattern.class)) {
                cTransition++;
            }
        }

        Assert.assertEquals(1, aTransition);
        Assert.assertEquals(1, bTransition);
        Assert.assertEquals(1, cTransition);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void nestedOrWithFollows1Test() {
        NestedOrWithFollows1_Pattern pattern = CepFactory.getInstance().createNestedOrWithFollows1_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        int orBrach = 0;
        int folBrach = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transition.getPostState().equals(automaton.getFinalStates().get(0))) {
                assertTransitionTypedWith(transition, C_Pattern.class);
                orBrach++;
            } else {
                assertTransitionTypedWith(transition, A_Pattern.class);
                folBrach++;
                State nextState = transition.getPostState();
                Assert.assertEquals(1, nextState.getOutTransitions().size());
                Transition transition2 = nextState.getOutTransitions().get(0);
                assertTransitionTypedWith(transition2, B_Pattern.class);
                Assert.assertTrue(transition2.getPostState().equals(automaton.getFinalStates().get(0)));
            }
        }

        Assert.assertEquals(1, orBrach);
        Assert.assertEquals(1, folBrach);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void nestedOrWithFollows2Test() {
        NestedOrWithFollows2_Pattern pattern = CepFactory.getInstance().createNestedOrWithFollows2_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(2, automaton.getInitialState().getOutTransitions().size());

        automaton.getInitialState().getOutTransitions().get(0)
                .equals(automaton.getInitialState().getOutTransitions().get(1));

        int aTransition = 0;
        int bTransition = 0;

        for (Transition transition : automaton.getInitialState().getOutTransitions()) {
            if (transitionTypedWith(transition, A_Pattern.class)) {
                aTransition++;
            } else if (transitionTypedWith(transition, B_Pattern.class)) {
                bTransition++;
            }
        }

        Assert.assertEquals(1, aTransition);
        Assert.assertEquals(1, bTransition);

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        Transition finalTransition = nextState.getOutTransitions().get(0);
        assertTransitionTypedWith(finalTransition, C_Pattern.class);
        Assert.assertTrue(finalTransition.getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }
}
