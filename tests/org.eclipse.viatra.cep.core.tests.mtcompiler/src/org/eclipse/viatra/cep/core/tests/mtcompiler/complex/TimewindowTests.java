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
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinAnd_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows3_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinOr2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinOr_Pattern;
import org.junit.Assert;
import org.junit.Test;

public class TimewindowTests extends ComplexTest {

    @Test
    public void timewinFollowsTest() {
        TimewinFollows_Pattern pattern = CepFactory.getInstance().createTimewinFollows_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(1, nextState.getOutTransitions().size());

        Assert.assertTrue(nextState.getOutTransitions().get(0).getPostState().equals(automaton.getFinalStates().get(0)));

        Assert.assertEquals(1, automaton.getTimedZones().size());
        Assert.assertTrue(automaton.getTimedZones().get(0).getInState().equals(nextState));
        Assert.assertTrue(automaton.getTimedZones().get(0).getOutState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void timewinFollows2Test() {
        TimewinFollows2_Pattern pattern = CepFactory.getInstance().createTimewinFollows2_Pattern();
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

        Assert.assertEquals(1, automaton.getTimedZones().size());
        Assert.assertTrue(automaton.getTimedZones().get(0).getInState().equals(nextState));
        Assert.assertTrue(automaton.getTimedZones().get(0).getOutState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void timewinFollows3Test() {
        TimewinFollows3_Pattern pattern = CepFactory.getInstance().createTimewinFollows3_Pattern();
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

        Assert.assertEquals(1, automaton.getTimedZones().size());
        Assert.assertTrue(automaton.getTimedZones().get(0).getInState().equals(nextState2));
        Assert.assertTrue(automaton.getTimedZones().get(0).getOutState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void timewinOrTest() {
        TimewinOr_Pattern pattern = CepFactory.getInstance().createTimewinOr_Pattern();
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

        Assert.assertEquals(2, automaton.getTimedZones().size());
        Assert.assertTrue(automaton.getTimedZones().get(0).getInState().equals(automaton.getInitialState()));
        Assert.assertTrue(automaton.getTimedZones().get(0).getOutState().equals(automaton.getFinalStates().get(0)));
        Assert.assertTrue(automaton.getTimedZones().get(1).getInState().equals(automaton.getInitialState()));
        Assert.assertTrue(automaton.getTimedZones().get(1).getOutState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void timewinOr2Test() {
        TimewinOr2_Pattern pattern = CepFactory.getInstance().createTimewinOr2_Pattern();
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

        Assert.assertEquals(2, automaton.getTimedZones().size());
        Assert.assertTrue(automaton.getTimedZones().get(0).getInState().equals(nextStateAfterA));
        Assert.assertTrue(automaton.getTimedZones().get(0).getOutState().equals(automaton.getFinalStates().get(0)));
        Assert.assertTrue(automaton.getTimedZones().get(1).getInState().equals(nextStateAfterB));
        Assert.assertTrue(automaton.getTimedZones().get(1).getOutState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }

    @Test
    public void timewinAndTest() {
        TimewinAnd_Pattern pattern = CepFactory.getInstance().createTimewinAnd_Pattern();
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

        Assert.assertEquals(2, automaton.getTimedZones().size());
        Assert.assertTrue(automaton.getTimedZones().get(0).getInState().equals(nextStateAfterA));
        Assert.assertTrue(automaton.getTimedZones().get(0).getOutState().equals(automaton.getFinalStates().get(0)));
        Assert.assertTrue(automaton.getTimedZones().get(1).getInState().equals(nextStateAfterB));
        Assert.assertTrue(automaton.getTimedZones().get(1).getOutState().equals(automaton.getFinalStates().get(0)));

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }
}
