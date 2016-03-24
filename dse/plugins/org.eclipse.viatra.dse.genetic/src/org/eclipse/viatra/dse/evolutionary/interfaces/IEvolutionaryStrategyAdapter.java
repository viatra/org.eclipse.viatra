package org.eclipse.viatra.dse.evolutionary.interfaces;

import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public interface IEvolutionaryStrategyAdapter {

    void iterationCompleted(List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation, Collection<TrajectoryFitness> survivedPopulation, boolean stop);
    
}
