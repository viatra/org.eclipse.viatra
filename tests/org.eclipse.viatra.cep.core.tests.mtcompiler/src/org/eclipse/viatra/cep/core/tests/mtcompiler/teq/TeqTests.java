package org.eclipse.viatra.cep.core.tests.mtcompiler.teq;

import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noEpsilonTransitions;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanStates;
import static org.eclipse.viatra.cep.core.tests.mtcompiler.complex.Utils.noOrphanTransitions;

import org.eclipse.viatra.cep.core.experimental.mtcompiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.CepFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Teq1_Pattern;
import org.eclipse.viatra.cep.core.tests.mtcompiler.complex.ComplexTest;
import org.junit.Assert;
import org.junit.Test;

public class TeqTests extends ComplexTest {

    @Test
    public void teq1Test() {
        Teq1_Pattern pattern = CepFactory.getInstance().createTeq1_Pattern();
        eventModel.getEventPatterns().add(pattern);

        Assert.assertEquals(0, internalModel.getAutomata().size());

        new TransformationBasedCompiler().compile(resourceSet);

        Assert.assertEquals(1, internalModel.getAutomata().size());

        Automaton automaton = internalModel.getAutomata().get(0);

        Assert.assertEquals(4, automaton.getStates().size());
        Assert.assertEquals(1, automaton.getInitialState().getOutTransitions().size());

        State nextState = automaton.getInitialState().getOutTransitions().get(0).getPostState();
        Assert.assertEquals(6, nextState.getOutTransitions().size());

        int finalTransition = 0;
        int selfTransitions = 0;

        for (Transition transition : nextState.getOutTransitions()) {
            if (transition.getPostState().equals(automaton.getFinalStates().get(0))) {
                finalTransition++;
            } else if (transition.getPostState().equals(nextState)) {
                selfTransitions++;
            } else {
                throw new IllegalArgumentException();
            }
        }

        Assert.assertEquals(1, finalTransition);
        Assert.assertEquals(5, selfTransitions);

        Assert.assertTrue(noEpsilonTransitions(automaton));
        Assert.assertTrue(noOrphanTransitions(automaton));
        Assert.assertTrue(noOrphanStates(automaton));
    }
}
