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
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
import org.eclipse.viatra.dse.genetic.crossovers.SwapTransitionCrossover;
import org.eclipse.viatra.dse.genetic.initialselectors.BFSSelector;
import org.eclipse.viatra.dse.genetic.initialselectors.FixedPrioritySelector;
import org.eclipse.viatra.dse.genetic.initialselectors.HillClimbingSelector;
import org.eclipse.viatra.dse.genetic.initialselectors.PredefinedPopulationSelector;
import org.eclipse.viatra.dse.genetic.initialselectors.RandomSearchSelector;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.mutations.AddRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.AddTransitionByPriorityMutation;
import org.eclipse.viatra.dse.genetic.mutations.DeleteRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.ModifyRandomTransitionMutation;
import org.eclipse.viatra.dse.genetic.mutations.ModifyTransitionByPriorityMutation;
import org.eclipse.viatra.dse.genetic.parentselectors.CrowdedTournementParentSelector;
import org.eclipse.viatra.dse.genetic.parentselectors.MyRoundRobinParentSelector;
import org.eclipse.viatra.dse.genetic.parentselectors.RandomParentSelector;
import org.eclipse.viatra.dse.genetic.selectors.NonDominatedAndCrowdingDistanceSelector;
import org.eclipse.viatra.dse.genetic.selectors.ParetoSelector;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.solutionstore.StrategyDependentSolutionStore;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

/**
 * This abstract class helps to test out genetic algorithms run by the {@link GeneticDesignSpaceExplorer} if inherited.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public abstract class GeneticTestRunner extends BaseTestRunner {

    private static final String EXCEPTIONS_FILE = "exceptions.txt";
    // Config
    private static final String CONFIG_NAME = "ConfigName";
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
    public static final String SWAP_TRANSITIONS_CROSSOVER = "SwapTransitionsCrossover";
    public static final String ADD_MUTATION = "AddMutation";
    public static final String ADD_BY_PRIORITY_MUTATION = "AddByPriorityMutation";
    public static final String MODIFY_MUTATION = "ModifyMutation";
    public static final String MODIFY_BY_PRIORITY_MUTATION = "ModifyByPriorityMutation";
    public static final String DELETE_MUTATION = "DeleteMutation";
    public static final String PARENT_SELECTOR = "ParentSelector";
    public static final String SELECTOR = "Selector";

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
     * @throws ViatraQueryException
     */
    public abstract void configDSE(Row configRow, DesignSpaceExplorer dse, GeneticStrategyBuilder builder) throws ViatraQueryException;

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
        
        configDSE(configRow, dse, builder);

        if (lastConfigId != result.configId) {
            lastConfigId = result.configId;

            geneticDebugger = getGeneticDebugger(dse.getGlobalContext());
            if (geneticDebugger == null) {
                geneticDebugger = new GeneticDebugger(true, dse.getGlobalContext());
            }
            geneticDebugger.setConfigId(result.configId);

            if (configRow.isKeyPresent(CONFIG_NAME)) {
                String configName = configRow.getValueAsString(CONFIG_NAME);
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

        if (configRow.isKeyPresent(PARENT_SELECTOR)) {
            String parentSelector = configRow.getValueAsString(PARENT_SELECTOR);
            if ("RoundRobin".equals(parentSelector)) {
                builder.setParentSelector(new MyRoundRobinParentSelector());
            } else if ("CrowdedTournament".equals(parentSelector)) {
                builder.setParentSelector(new CrowdedTournementParentSelector());
            } else if ("Random".equals(parentSelector)) {
                builder.setParentSelector(new RandomParentSelector());
            } else {
                throw new GeneticConfigurationException("Unsupported parent selector: " + parentSelector);
            }
        }

        if (configRow.isKeyPresent(SELECTOR)) {
            String selector = configRow.getValueAsString(SELECTOR);
            if ("NSGA-II".equals(selector)) {
                builder.setSelector(new NonDominatedAndCrowdingDistanceSelector());
            } else if (selector.startsWith("Pareto")) {
                if (selector.contains("-")) {
                    String maxPopSizeString = selector.substring(selector.indexOf('-')+1);
                    int maxPopulationSize = Integer.parseInt(maxPopSizeString);
                    builder.setSelector(new ParetoSelector().withMaxPopulationSize(maxPopulationSize));
                } else {
                    builder.setSelector(new ParetoSelector());
                }
            } else {
                throw new GeneticConfigurationException("Unsupported selector: " + selector);
            }
        }
        
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
        if (initialSelector.startsWith("BFS")) {
            float initialSelectionRate = configRow.getValueAsFloat(INITIAL_SELECTION_RATE);
            builder.setInitialPopulationSelector(new BFSSelector(initialSelectionRate));
            if (initialSelector.contains("-")) {
                String fileName = initialSelector.substring(initialSelector.indexOf('-')+1);
                builder.getStrategy().setTrajectoriesFileName(fileName);
            }
        } else if (initialSelector.startsWith("Priority")) {
            FixedPrioritySelector selector = new FixedPrioritySelector();
            selector.withPriorities(gso.priorities);
            builder.setInitialPopulationSelector(selector);
            if (initialSelector.contains("-")) {
                String fileName = initialSelector.substring(initialSelector.indexOf('-')+1);
                builder.getStrategy().setTrajectoriesFileName(fileName);
            }
        } else if (initialSelector.contains("HillClimbing")) {
            HillClimbingSelector selector = new HillClimbingSelector();
            selector.withTrialsToMake(configRow.getValueAsInteger(INITIAL_SELECTION_RATE));
            builder.setInitialPopulationSelector(selector);
            if (initialSelector.contains("-")) {
                String fileName = initialSelector.substring(initialSelector.indexOf('-')+1);
                builder.getStrategy().setTrajectoriesFileName(fileName);
            }
        } else if (initialSelector.contains("Random")) {
            String minAndMaxDepth = configRow.getValueAsString(INITIAL_SELECTION_RATE);
            String[] tokens = minAndMaxDepth.split("-");
            int minDepth = Integer.parseInt(tokens[0]);
            int maxDepth = Integer.parseInt(tokens[1]);
            RandomSearchSelector selector = new RandomSearchSelector(minDepth, maxDepth);
            builder.setInitialPopulationSelector(selector);
            if (initialSelector.contains("-")) {
                String fileName = initialSelector.substring(initialSelector.indexOf('-')+1);
                builder.getStrategy().setTrajectoriesFileName(fileName);
            }
        } else if (initialSelector.contains("Predefined-")) {
            String fileName = initialSelector.substring(initialSelector.indexOf('-')+1);
            builder.setInitialPopulationSelector(new PredefinedPopulationSelector(fileName));
        } else {
            throw new GeneticConfigurationException("No such initial selector: " + initialSelector);
        }

        int cutAndSplice = configRow.getValueAsInteger(CUT_AND_SPLICE_CROSSOVER);
        int singlePoint = configRow.getValueAsInteger(SINGLE_POINT_CROSSOVER);
        int permutation = configRow.getValueAsInteger(PERMUTATION_CROSSOVER);
        int swap = configRow.getValueAsInteger(SWAP_TRANSITIONS_CROSSOVER);
        builder.addCrossover(new CutAndSpliceCrossover(), cutAndSplice);
        builder.addCrossover(new OnePointCrossover(), singlePoint);
        builder.addCrossover(new PermutationEncodingCrossover(), permutation);
        builder.addCrossover(new SwapTransitionCrossover(), swap);

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
        boolean wasTimeout = dse.startExplorationWithTimeout(builder.getStrategy(), timeout * 1000);
        long end = System.nanoTime();

        result.runTime = (end - start) / 1000000000d;

        if (builder.getStrategy().getTrajectoriesFileName() != null) {
            builder.getStrategy().saveTrajectoriesToFile();
        }
        
        Row resultsRow = new Row(resultKeysInOrder);

        Collection<Throwable> exceptions = dse.getGlobalContext().getExceptions();
        if (!exceptions.isEmpty()) {
            result.report = "Exception happend. See " + EXCEPTIONS_FILE + " for details.";
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(EXCEPTIONS_FILE, true)));
                String configName = " ";
                if (configRow.isKeyPresent(CONFIG_NAME)) {
                    configName += configRow.getValueAsString(CONFIG_NAME);
                }
                out.println("Exceptions in config id:" + result.configId + configName + ", run id: " + result.runId);
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
                if (out != null) {
                    out.close();
                }
            }
        } else if (wasTimeout) {
            result.report = "Timeout";
        }

        Map<InstanceData, SolutionTrajectory> solutions = builder.getSolutions();

        if (solutions.isEmpty()) {
            if (!exceptions.isEmpty()) {
                return "";
            }
            Logger.getLogger(this.getClass().getSimpleName()).warn("Solution collection was empty.");
            return "No Solution found.";
        }

        Map<String, Double> avgObjectives = new HashMap<String, Double>();
        for (IObjective objective : dse.getGlobalContext().getObjectives()) {
            avgObjectives.put(objective.getName(), 0d);
        }
        for (InstanceData solution : solutions.keySet()) {
            addMaps(avgObjectives, solution.objectives);
        }
        for (Entry<String, Double> entry : avgObjectives.entrySet()) {
            Double d = entry.getValue();
            avgObjectives.put(entry.getKey(), d / solutions.size());
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
            for (Entry<String, Double> entry : addMap.entrySet()) {
                Double d = entry.getValue();
                if (d == null) {
                    throw new DSEException(entry.getKey() + " is missing.");
                }
                baseMap.put(entry.getKey(), d + entry.getValue());
            }
        }
    }
    
}
