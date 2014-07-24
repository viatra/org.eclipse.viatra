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
package org.eclipse.viatra.dse.genetic.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.genetic.api.GeneticDesignSpaceExplorer;
import org.eclipse.viatra.dse.genetic.core.GeneticSharedObject;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.crossovers.CutAndSpliceCrossover;
import org.eclipse.viatra.dse.genetic.crossovers.OnePointCrossover;
import org.eclipse.viatra.dse.genetic.crossovers.PermutationEncodingCrossover;
import org.eclipse.viatra.dse.genetic.debug.GeneticDebugger;
import org.eclipse.viatra.dse.genetic.initialselectors.BFSSelector;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.mutations.AddRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.AddTransitionByPriorityMutation;
import org.eclipse.viatra.dse.genetic.mutations.DeleteRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.ModifyRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.ModifyTransitionByPriorityMutation;

public abstract class GeneticTestRunner extends BaseTestRunner {

    // Config
    private static final String MODEL_PATH = "ModelPath";
    private static final String TIMEOUT = "Timeout[s]";
    private static final String POPULATION_SIZE = "PopulationSize";
    private static final String MUTATION_RATE = "MutationRate";
    private static final String BFS_SELECTION_RATE = "BfsSelectionRate";
    private static final String ITERATIONS = "Iterations";
    private static final String CROSSOVER = "Crossover";
    private static final String MUTATION = "Mutation";

    // Results
    private static final String SOLUTIONS = "Solutions";
    private static final String SOFT_CONSTRAINT = "SoftConstraint";
    private static final String AVG = "Avg";
    private static final String NUMBER_OF_CORRECTIONS = "NumberOfCorrections";
    private static final String NUMBER_OF_DUPLICATIONS = "NumberOfDuplications";

    private List<String> resultKeysInOrder;
    private boolean isFirstRun = true;

    public abstract GeneticDesignSpaceExplorer createGdse() throws IncQueryException;

    public abstract void registerXMISerailizer();

    private void addKeysToResultHeader(GeneticDesignSpaceExplorer gdse) {
        resultKeysInOrder = new ArrayList<String>();
        resultKeysInOrder.add(SOLUTIONS);
        resultKeysInOrder.add(SOFT_CONSTRAINT);
        for (String key : gdse.getGeneticSharedObject().comparators.keySet()) {
            resultKeysInOrder.add(AVG + key);
        }
        for (IMutateTrajectory mutator : gdse.getGeneticSharedObject().mutatiors) {
            resultKeysInOrder.add(mutator.getClass().getSimpleName());
        }
        for (ICrossoverTrajectories crossover : gdse.getGeneticSharedObject().crossovers) {
            resultKeysInOrder.add(crossover.getClass().getSimpleName());
        }
        resultKeysInOrder.add(NUMBER_OF_DUPLICATIONS);
        resultKeysInOrder.add(NUMBER_OF_CORRECTIONS);
    }

    @Override
    public String getResultsHeader() {
        StringBuilder sb = new StringBuilder();
        for (String string : resultKeysInOrder) {
            sb.append(string);
            sb.append(';');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String runTestWithConfig(Row configRow, BaseResult result, int configId, int runId) throws IncQueryException {

        GeneticDesignSpaceExplorer gdse = createGdse();

        GeneticDebugger geneticDebugger = new GeneticDebugger(true);
        geneticDebugger.setConfigId(configId);
        geneticDebugger.setRunId(runId);
        gdse.setDebugger(geneticDebugger);

        registerXMISerailizer();

        ResourceSet resSet = new ResourceSetImpl();
        Resource resource = resSet.getResource(URI.createURI(configRow.getValueAsString(MODEL_PATH)), true);
        EObject root = resource.getContents().get(0);

        gdse.setStartingModel(root);

        gdse.setMutationChanceAtCrossover(configRow.getValueAsFloat(MUTATION_RATE));
        gdse.setNumberOfPopulation(configRow.getValueAsInteger(ITERATIONS));

        int sizeOfPopulation = configRow.getValueAsInteger(POPULATION_SIZE);
        gdse.setSizeOfPopulation(sizeOfPopulation);

        float BfsSelectionRate = configRow.getValueAsFloat(BFS_SELECTION_RATE);
        gdse.setInitialPopulationSelector(new BFSSelector(BfsSelectionRate));

        String crossoverDef = configRow.getValueAsString(CROSSOVER);
        String[] crossovers = crossoverDef.split("\\+");
        for (String crossover : crossovers) {
            if ("CutAndSplice".equals(crossover) || "2".equals(crossover) || "2P".equals(crossover)) {
                gdse.addCrossover(new CutAndSpliceCrossover());
            } else if ("SinglePoint".equals(crossover) || "1".equals(crossover) || "1P".equals(crossover)) {
                gdse.addCrossover(new OnePointCrossover());
            } else if ("Permutation".equals(crossover) || "P".equals(crossover)) {
                gdse.addCrossover(new PermutationEncodingCrossover());
            }
        }

        String mutationDef = configRow.getValueAsString(MUTATION);
        String[] mutations = mutationDef.split("\\+");
        for (String mutation : mutations) {
            if ("Add".equals(mutation)) {
                gdse.addMutatitor(new AddRandomTransitionMutation());
            } else if ("AddByPriority".equals(mutation)) {
                gdse.addMutatitor(new AddTransitionByPriorityMutation());
            } else if ("Delete".equals(mutation)) {
                gdse.addMutatitor(new DeleteRandomTransitionMutation());
            } else if ("Modify".equals(mutation)) {
                gdse.addMutatitor(new ModifyRandomTransitionMutation());
            } else if ("ModifyByPriority".equals(mutation)) {
                gdse.addMutatitor(new ModifyTransitionByPriorityMutation());
            }
        }

        long timeout = configRow.getValueAsLong(TIMEOUT);

        if (isFirstRun) {
            addKeysToResultHeader(gdse);
            isFirstRun = false;
        }

        long start = System.nanoTime();
        boolean wasTimeout = gdse.startExploration(timeout * 1000);
        long end = System.nanoTime();

        result.runTime = (end - start) / 1000000000d;

        Row resultsRow = new Row(resultKeysInOrder);

        boolean exceptionHappened = gdse.getDseEngine().getGlobalContext().getExceptionHappendInOtherThread().get();
        if (exceptionHappened) {
            result.report = "Exception happend";
        } else if (wasTimeout) {
            result.report = "Timeout";
        } else {

            Map<InstanceData, SolutionTrajectory> solutions = gdse.getSolutions();

            if (solutions.isEmpty()) {
                result.report = "Empty solutions";
            } else {

                GeneticSharedObject gso = gdse.getGeneticSharedObject();

                Map<String, Double> avgObjectives = new HashMap<String, Double>();
                for (String key : gso.comparators.keySet()) {
                    avgObjectives.put(key, 0d);
                }
                for (InstanceData solution : solutions.keySet()) {
                    addMaps(avgObjectives, solution.objectives);
                }
                for (String key : avgObjectives.keySet()) {
                    Double d = avgObjectives.get(key);
                    avgObjectives.put(key, d / solutions.size());
                }

                resultsRow.add(SOLUTIONS, solutions.size());
                InstanceData aSolution = solutions.keySet().iterator().next();
                resultsRow.add(SOFT_CONSTRAINT, aSolution.sumOfConstraintViolationMeauserement);

                for (String objective : avgObjectives.keySet()) {
                    resultsRow.add(AVG + objective, avgObjectives.get(objective));
                }

                for (IMutateTrajectory mutator : gso.mutationApplications.keySet()) {
                    resultsRow.add(mutator.getClass().getSimpleName(), gso.mutationApplications.get(mutator));
                }
                for (ICrossoverTrajectories crossover : gso.crossoverApplications.keySet()) {
                    resultsRow.add(crossover.getClass().getSimpleName(), gso.crossoverApplications.get(crossover));
                }

                resultsRow.add(NUMBER_OF_DUPLICATIONS, gso.numOfDuplications);
                resultsRow.add(NUMBER_OF_CORRECTIONS, gso.numOfCorrections.get());

            }
        }

        return resultsRow.resultString();
    }

    private void addMaps(Map<String, Double> baseMap, Map<String, Double> addMap) {
        if (addMap != null) {
            for (String key : addMap.keySet()) {
                Double d = baseMap.get(key);
                if (d == null) {
                    throw new DSEException(key + " is missing.");
                }
                baseMap.put(key, d + addMap.get(key));
            }
        }
    }

}
