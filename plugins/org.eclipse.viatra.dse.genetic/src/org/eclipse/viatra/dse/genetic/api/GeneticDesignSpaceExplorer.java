/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.api;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.DesignSpaceExplorer;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.api.strategy.StrategyBase;
import org.eclipse.viatra.dse.genetic.core.GeneticSharedObject;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.core.MainGeneticStrategy;
import org.eclipse.viatra.dse.genetic.core.SoftConstraint;
import org.eclipse.viatra.dse.genetic.debug.GeneticDebugger;
import org.eclipse.viatra.dse.genetic.interfaces.ICalculateModelObjectives;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IFitnessCalculator;
import org.eclipse.viatra.dse.genetic.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.interfaces.ISelectNextPopulation;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

public class GeneticDesignSpaceExplorer {

    private final MainGeneticStrategy MAIN_GENETIC_STRATEGY = new MainGeneticStrategy();
    private DesignSpaceExplorer dse;
    private GeneticSharedObject configuration;
    private Guidance guidance;

    public GeneticDesignSpaceExplorer() {
        dse = new DesignSpaceExplorer();
        configuration = new GeneticSharedObject();
        dse.setSharedObject(configuration);
    }

    public void setStartingModel(EObject root) {
        dse.setStartingModel(root, true);
    }

    public void setSerializerFactory(IStateSerializerFactory serializerFactory) {
        dse.setSerializerFactory(serializerFactory);
    }

    public void addTransformationRule(TransformationRule<? extends IPatternMatch> rule) {
        dse.addTransformationRule(rule);
    }

    public void addTransformationRule(TransformationRule<? extends IPatternMatch> rule, int priority) {
        dse.addTransformationRule(rule);
        if (guidance == null) {
            guidance = new Guidance();
        }
        guidance.addPriorityRuleInfo(rule, priority);
    }

    public void setInitialPopulationSelector(IInitialPopulationSelector selector) {
        configuration.initialPopulationSelector = selector;
    }

    public void setSizeOfPopulation(int sizeOfPopulation) {
        configuration.sizeOfPopulation = sizeOfPopulation;
    }

    public void setNumberOfPopulation(int numberOfPopulation) {
        configuration.maxNumberOfPopulation = numberOfPopulation;
    }

    public void addSoftConstraint(SoftConstraint softConstraint) {
        configuration.softConstraints.add(softConstraint);
    }

    public void addGlobalConstraint(PatternWithCardinality constraint) {
        dse.addConstraint(constraint);
    }

    public void addObjectiveComparator(String objectiveName, Comparator<InstanceData> comparator) {
        configuration.comparators.put(objectiveName, comparator);
    }

    public void setModelObjectiveCalculator(ICalculateModelObjectives calculator) {
        configuration.modelObjectivesCalculator = calculator;
    }

    public void setMutationChanceAtCrossover(float propability) {
        configuration.chanceOfMutationInsteadOfCrossover = propability;
    }

    public void addMutatitor(IMutateTrajectory mutatior) {
        addMutatitor(mutatior, 1);
    }

    public void addMutatitor(IMutateTrajectory mutatior, int weight) {
        configuration.mutationApplications.put(mutatior, 0);
        for (int i = 0; i < weight; ++i) {
            configuration.mutatiors.add(mutatior);
        }
    }

    public void addCrossover(ICrossoverTrajectories crossover) {
        addCrossover(crossover, 1);
    }

    public void addCrossover(ICrossoverTrajectories crossover, int weight) {
        configuration.crossoverApplications.put(crossover, 0);
        for (int i = 0; i < weight; ++i) {
            configuration.crossovers.add(crossover);
        }
    }

    public void setSelector(ISelectNextPopulation selector) {
        configuration.selector = selector;
    }

    public void setFitnessCalculator(IFitnessCalculator fitnessCalculator) {
        configuration.fitnessCalculator = fitnessCalculator;
    }

    public void setNumberOfWorkerThreads(int workerThreads) {
        configuration.workerThreads = workerThreads;
    }

    public void startExploration() {
        this.startExploration(true);
    }

    public void startExploration(boolean waitForTermination) {

        if (configuration.crossovers.isEmpty()) {
            throw new DSEException("There is no crossover operation registered.");
        }
        if (configuration.mutatiors.isEmpty()) {
            throw new DSEException("There is no mutation operation registered.");
        }

        if (guidance != null) {
            dse.setGuidance(guidance);
        }
        dse.startExploration(new StrategyBase(MAIN_GENETIC_STRATEGY), waitForTermination);
    }

    public boolean startExploration(long timeOutInMiliSec) {

        if (configuration.crossovers.isEmpty()) {
            throw new DSEException("There is no crossover operation registered.");
        }
        if (configuration.mutatiors.isEmpty()) {
            throw new DSEException("There is no mutation operation registered.");
        }

        if (guidance != null) {
            dse.setGuidance(guidance);
        }

        dse.startExploration(new StrategyBase(MAIN_GENETIC_STRATEGY), false);

        double start = System.nanoTime() / 1000000;
        do {
            try {
                Thread.sleep(10);
                if (dse.getGlobalContext().isDone()) {
                    return false;
                }
            } catch (InterruptedException e) {
            }
        } while ((System.nanoTime() / 1000000) - start < timeOutInMiliSec);

        dse.getGlobalContext().stopAllThreads();

        // wait until all threads exit
        do {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }

            if (dse.getGlobalContext().isDone()) {
                return true;
            }
        } while (true);

    }

    public String getSolutionsInString() {
        StringBuilder sb = new StringBuilder();
        Set<InstanceData> solutions = getSolutions().keySet();

        sb.append("Best solutions (" + solutions.size() + ")");
        int i = 1;
        for (InstanceData instanceData : solutions) {
            sb.append("\n" + i++ + ". solution\n");
            instanceData.prettyPrint(sb);
            String state = instanceData.trajectory.get(instanceData.trajectory.size() - 1).getResultsIn().getId()
                    .toString();
            int runningApps = state.split("RUNNING").length - 1;
            int apps = state.split("\\{").length - 1;
            int unallocated = state.split("\\(\\)").length - 1;
            int unusedHosts = state.substring(state.lastIndexOf('}')).split(",").length;
            sb.append("running/unallocated/all: " + runningApps + "/" + unallocated + "/" + apps);
            sb.append(", unusedHosts: " + unusedHosts);
        }

        return sb.toString();
    }

    public DesignSpaceExplorer getDseEngine() {
        return dse;
    }

    public GeneticSharedObject getGeneticSharedObject() {
        return configuration;
    }

    public Guidance getGuidance() {
        return guidance;
    }

    public Map<InstanceData, SolutionTrajectory> getSolutions() {
        return configuration.bestSolutions;
    }

    public void setDebugger(GeneticDebugger geneticDebugger) {
        MAIN_GENETIC_STRATEGY.setGeneticDebugger(geneticDebugger);
    }
}
