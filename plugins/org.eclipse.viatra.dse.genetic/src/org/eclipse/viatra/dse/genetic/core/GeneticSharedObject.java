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
package org.eclipse.viatra.dse.genetic.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.genetic.fintesscalculators.SimpleFitnessCalculator;
import org.eclipse.viatra.dse.genetic.interfaces.ICalculateModelObjectives;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IFitnessCalculator;
import org.eclipse.viatra.dse.genetic.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.interfaces.ISelectNextPopulation;
import org.eclipse.viatra.dse.genetic.selectors.NonDominatedAndCrowdingDistanceSelector;

public class GeneticSharedObject {

    // Configuration
    public int sizeOfPopulation = 4;
    public int maxNumberOfPopulation = 50;
    public int actNumberOfPopulation = 1;
    public float chanceOfMutationInsteadOfCrossover = 0.2f;
    public float mutationChanceMultiplier = 0.0f;
    public int workerThreads = 0;

    public List<SoftConstraint> softConstraints = new ArrayList<SoftConstraint>();
    public Map<String, Comparator<InstanceData>> comparators = new HashMap<String, Comparator<InstanceData>>();
    public List<IMutateTrajectory> mutatiors = new ArrayList<IMutateTrajectory>();
    public List<ICrossoverTrajectories> crossovers = new ArrayList<ICrossoverTrajectories>();
    public ISelectNextPopulation selector = new NonDominatedAndCrowdingDistanceSelector();
    public ICalculateModelObjectives modelObjectivesCalculator;
    public IInitialPopulationSelector initialPopulationSelector;
    public IFitnessCalculator fitnessCalculator = new SimpleFitnessCalculator();

    // Basic information
    public EObject initialModel;

    // Thread managing
    public volatile ConcurrentLinkedQueue<InstanceData> childPopulation = new ConcurrentLinkedQueue<InstanceData>();
    public volatile ArrayBlockingQueue<InstanceData> instancesToBeChecked;
    public AtomicInteger unfeasibleInstances = new AtomicInteger(0);
    public AtomicBoolean newPopulationIsNeeded = new AtomicBoolean(true);
    public AtomicBoolean addInstanceToBestSolutions = new AtomicBoolean(false);

    // Result
    public Map<InstanceData, SolutionTrajectory> bestSolutions = new ConcurrentHashMap<InstanceData, SolutionTrajectory>();
    public Map<IMutateTrajectory, Integer> mutationApplications = new HashMap<IMutateTrajectory, Integer>();
    public Map<ICrossoverTrajectories, Integer> crossoverApplications = new HashMap<ICrossoverTrajectories, Integer>();
    public AtomicInteger numOfCorrections = new AtomicInteger(0);
    public int numOfDuplications = 0;

    public void mutationUsed(IMutateTrajectory mutator) {
        Integer integer = mutationApplications.get(mutator);
        mutationApplications.put(mutator, integer + 1);
    }

    public void crossoverUsed(ICrossoverTrajectories crossover) {
        Integer integer = crossoverApplications.get(crossover);
        crossoverApplications.put(crossover, integer + 1);
    }

}
