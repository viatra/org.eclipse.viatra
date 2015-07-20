package org.eclipse.viatra.cep.core.tests.mtcompiler.complex;

import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noEpsilonTransitions;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanStates;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanTransitions;

import org.eclipse.viatra.cep.core.experimental.mtcompiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.CepFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows_Pattern;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TimewindowTests extends ComplexTest {

    @Ignore
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

    @Ignore
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
}
