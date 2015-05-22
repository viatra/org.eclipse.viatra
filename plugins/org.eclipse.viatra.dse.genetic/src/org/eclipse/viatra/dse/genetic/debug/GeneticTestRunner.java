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
package org.eclipse.viatra.dse.genetic.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.DesignSpaceExplorer;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.genetic.api.GeneticStrategyBuilder;
import org.eclipse.viatra.dse.genetic.api.StopCondition;
import org.eclipse.viatra.dse.genetic.core.GeneticSharedObject;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.crossovers.CutAndSpliceCrossover;
import org.eclipse.viatra.dse.genetic.crossovers.OnePointCrossover;
import org.eclipse.viatra.dse.genetic.crossovers.PermutationEncodingCrossover;
import org.eclipse.viatra.dse.genetic.initialselectors.BFSSelector;
import org.eclipse.viatra.dse.genetic.initialselectors.FixedPrioritySelector;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.mutations.AddRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.AddTransitionByPriorityMutation;
import org.eclipse.viatra.dse.genetic.mutations.DeleteRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.ModifyRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.ModifyTransitionByPriorityMutation;
import org.eclipse.viatra.dse.genetic.selectors.NonDominatedAndCrowdingDistanceSelector;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.solutionstore.StrategyDependentSolutionStore;

/**
 * This abstract class helps to test out genetic algorithms run by the {@link GeneticDesignSpaceExplorer} if inherited.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public abstract class GeneticTestRunner extends BaseTestRunner {

    private static final String EXCEPTIONS_FILE = "exceptions.txt";
    // Config
    public static final String MODEL_PATH = "InitialModelPath";
    public static final String TIMEOUT = "Timeout[s]";
    public static final String POPULATION_SIZE = "PopulationSize";
    public static final String MUTATION_RATE = "MutationRate";
    public static final String ADAPTIVE_MUTATION_MULTIPLIER = "AdaptiveMutationMultiplier";
    public static final String INITIAL_SELECTOR = "InitialSelector";
    public static final String INITIAL_SELECTION_RATE = "InitialSelectionRate";
    public static final String STOP_CONDITION = "StopCondition";
    public static final String STOP_CONDITION_NUMBER = "StopConditionNumber";
    public static final String CUT_AND_SPLICE_CROSSOVER = "CutAndSpliceCrossover";
    public static final String SINGLE_POINT_CROSSOVER = "SinglePointCrossover";
    public static final String PERMUTATION_CROSSOVER = "PermutationCrossover";
    public static final String ADD_MUTATION = "AddMutation";
    public static final String ADD_BY_PRIORITY_MUTATION = "AddByPriorityMutation";
    public static final String MODIFY_MUTATION = "ModifyMutation";
    public static final String MODIFY_BY_PRIORITY_MUTATION = "ModifyByPriorityMutation";
    public static final String DELETE_MUTATION = "DeleteMutation";

    // Results
    public static final String SOLUTIONS = "Solutions";
    public static final String AVG = "Avg";
    public static final String NUMBER_OF_CORRECTIONS = "NumberOfCorrections";
    public static final String NUMBER_OF_DUPLICATIONS = "NumberOfDuplications";

    private List<String> resultKeysInOrder;
    private boolean isFirstRun = true;
    private GeneticDebugger geneticDebugger;
    private int lastConfigId = -1;

    /**
     * Creates a {@link GeneticDesignSpaceExplorer} configured with the transformations, objectives, serializer, etc.
     * 
     * @param configRow
     *            The corrsponding row from the configuration csv file.
     * @return The configured {@link GeneticDesignSpaceExplorer}.
     * @throws IncQueryException
     */
    public abstract void configDSE(Row configRow, DesignSpaceExplorer dse, GeneticStrategyBuilder builder) throws IncQueryException;

    /**
     * If needed, an XMI serializer can be registered here, for loading the test models.
     */
    public abstract void registerXMISerailizer();

    /**
     * Custom result columns can be registered by the implementation.
     * 
     * @return A list of string containing the column names.
     */
    public abstract List<String> getCustomResultColumns();

    /**
     * The implementation adds results, if custom result columns are defined.
     * 
     * @param configRow
     *            The row containing the configuration.
     * @param resultsRow
     *            Add results to this row.
     */
    public abstract void addResults(Row configRow, Row resultsRow);

    /**
     * Custom {@link GeneticDebugger} can be registered by returning an instance in the implementation.
     * @param globalContext 
     * 
     * @return An instance of {@link GeneticDebugger}.
     */
    public abstract GeneticDebugger getGeneticDebugger(GlobalContext globalContext);

    private void addKeysToResultHeader(GeneticSharedObject gso, DesignSpaceExplorer dse) {
        resultKeysInOrder = new ArrayList<String>();
        resultKeysInOrder.add(SOLUTIONS);
        for (IObjective objective : dse.getGlobalContext().getObjectives()) {
            resultKeysInOrder.add(AVG + objective.getName());
        }
        for (IMutateTrajectory mutator : gso.mutationApplications.keySet()) {
            resultKeysInOrder.add(mutator.getClass().getSimpleName());
        }
        for (ICrossoverTrajectories crossover : gso.crossoverApplications.keySet()) {
            resultKeysInOrder.add(crossover.getClass().getSimpleName());
        }
        resultKeysInOrder.add(NUMBER_OF_DUPLICATIONS);
        resultKeysInOrder.add(NUMBER_OF_CORRECTIONS);

        List<String> customResultColumns = getCustomResultColumns();
        if (customResultColumns != null) {
            for (String key : customResultColumns) {
                resultKeysInOrder.add(key);
            }
        }
    }

    @Override
    public String getResultsHeader() {
        StringBuilder sb = new StringBuilder();
        for (String string : resultKeysInOrder) {
            sb.append(string);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String runTestWithConfig(Row configRow, BaseResult result) throws Exception {

        
        DesignSpaceExplorer dse = new DesignSpaceExplorer();
        GeneticStrategyBuilder builder = new GeneticStrategyBuilder();
        GeneticSharedObject gso = builder.getSharedObject();
        
        dse.setSolutionStore(new StrategyDependentSolutionStore());
        builder.setSelector(new NonDominatedAndCrowdingDistanceSelector());
        
        configDSE(configRow, dse, builder);

        if (lastConfigId != result.configId) {
            lastConfigId = result.configId;

            geneticDebugger = getGeneticDebugger(dse.getGlobalContext());
            if (geneticDebugger == null) {
                geneticDebugger = new GeneticDebugger(true, dse.getGlobalContext());
            }
            geneticDebugger.setConfigId(result.configId);

            if (configRow.isKeyPresent("ConfigName")) {
                String configName = configRow.getValueAsString("ConfigName");
                geneticDebugger.setCsvName(resultsFolderName + File.separator + configName + ".csv");
            } else {
                geneticDebugger.setCsvName(resultsFolderName
                        + File.separator
                        +"results-"
                        + Integer.toString(result.configId)
                        + ".csv");
            }
        } else {
            geneticDebugger.resetIteration();
        }

        geneticDebugger.setRunId(result.runId);
        builder.setDebugger(geneticDebugger);

        registerXMISerailizer();

        ResourceSet resSet = new ResourceSetImpl();
        Resource resource = resSet.getResource(URI.createURI(configRow.getValueAsString(MODEL_PATH)), true);
        EObject root = resource.getContents().get(0);

        dse.setInitialModel(root);

        float mutationChance = configRow.getValueAsFloat(MUTATION_RATE);
        float mutationMultiplier = configRow.getValueAsFloat(ADAPTIVE_MUTATION_MULTIPLIER);
        builder.setMutationChanceAtCrossover(mutationChance, mutationMultiplier);

        String valueAsString = configRow.getValueAsString(STOP_CONDITION);
        StopCondition stopCondition = StopCondition.valueOf(valueAsString);
        int stopConditionNumber = configRow.getValueAsInteger(STOP_CONDITION_NUMBER);
        builder.setStopCondition(stopCondition, stopConditionNumber);

        int sizeOfPopulation = configRow.getValueAsInteger(POPULATION_SIZE);
        builder.setSizeOfPopulation(sizeOfPopulation);

        String initialSelector = configRow.getValueAsString(INITIAL_SELECTOR);
        float initialSelectionRate = configRow.getValueAsFloat(INITIAL_SELECTION_RATE);
        if ("BFS".equals(initialSelector)) {
            builder.setInitialPopulationSelector(new BFSSelector(initialSelectionRate));
        } else if ("Priority".equals(initialSelector)) {
            builder.setInitialPopulationSelector(new FixedPrioritySelector());
        } else {
            throw new GeneticConfigurationException("No such initial selector: " + initialSelector);
        }

        int cutAndSplice = configRow.getValueAsInteger(CUT_AND_SPLICE_CROSSOVER);
        int singlePoint = configRow.getValueAsInteger(SINGLE_POINT_CROSSOVER);
        int permutation = configRow.getValueAsInteger(PERMUTATION_CROSSOVER);
        builder.addCrossover(new CutAndSpliceCrossover(), cutAndSplice);
        builder.addCrossover(new OnePointCrossover(), singlePoint);
        builder.addCrossover(new PermutationEncodingCrossover(), permutation);

        int add = configRow.getValueAsInteger(ADD_MUTATION);
        int addByPriority = configRow.getValueAsInteger(ADD_BY_PRIORITY_MUTATION);
        int delete = configRow.getValueAsInteger(DELETE_MUTATION);
        int modify = configRow.getValueAsInteger(MODIFY_MUTATION);
        int modifyByPriority = configRow.getValueAsInteger(MODIFY_BY_PRIORITY_MUTATION);
        builder.addMutatitor(new AddRandomTransitionMutation(), add);
        builder.addMutatitor(new AddTransitionByPriorityMutation(), addByPriority);
        builder.addMutatitor(new DeleteRandomTransitionMutation(), delete);
        builder.addMutatitor(new ModifyRandomTransitionMutation(), modify);
        builder.addMutatitor(new ModifyTransitionByPriorityMutation(), modifyByPriority);

        long timeout = configRow.getValueAsLong(TIMEOUT);

        if (isFirstRun) {
            addKeysToResultHeader(gso, dse);
            isFirstRun = false;
        }

        long start = System.nanoTime();
        boolean wasTimeout = dse.startExplorationWithTimeout(builder.getStrategy(), timeout * 1000);;
        long end = System.nanoTime();

        result.runTime = (end - start) / 1000000000d;

        Row resultsRow = new Row(resultKeysInOrder);

        Collection<Throwable> exceptions = dse.getGlobalContext().getExceptions();
        if (!exceptions.isEmpty()) {
            result.report = "Exception happend. See " + EXCEPTIONS_FILE + " for details.";
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(EXCEPTIONS_FILE, true)));
                out.println("Exceptions in config id:" + result.configId + ", run id: " + result.runId);
                for (Throwable throwable : exceptions) {
                    StringWriter sw = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(sw));
                    out.println();
                    out.println(throwable.getMessage());
                    out.println();
                    out.println(sw.toString());
                }
                out.println("==========================================");
            } catch (IOException e) {
                throw e;
            } finally {
                out.close();
            }
        } else if (wasTimeout) {
            result.report = "Timeout";
        }

        Map<InstanceData, SolutionTrajectory> solutions = builder.getSolutions();

        if (solutions.isEmpty()) {
            if (!exceptions.isEmpty()) {
                return "";
            }
            Logger.getLogger(this.getClass().getSimpleName()).error("Solution collection was empty. It's a bug.");
            return "No Solution found its a bug";
        }

        Map<String, Double> avgObjectives = new HashMap<String, Double>();
        for (IObjective objective : dse.getGlobalContext().getObjectives()) {
            avgObjectives.put(objective.getName(), 0d);
        }
        for (InstanceData solution : solutions.keySet()) {
            addMaps(avgObjectives, solution.objectives);
        }
        for (String key : avgObjectives.keySet()) {
            Double d = avgObjectives.get(key);
            avgObjectives.put(key, d / solutions.size());
        }

        resultsRow.add(SOLUTIONS, solutions.size());

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

        addResults(configRow, resultsRow);

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
