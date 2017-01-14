/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.initialselectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.TrajectoryWithStateFitness;
import org.eclipse.viatra.dse.evolutionary.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;
import org.eclipse.viatra.transformation.evm.specific.ConflictResolvers;
import org.eclipse.viatra.transformation.evm.specific.resolver.FixedPriorityConflictResolver;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;

import com.google.common.base.Preconditions;

/**
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class FixedPriorityInitialSelector implements IInitialPopulationSelector {

    private ThreadContext context;
    private DesignSpaceManager dsm;
    private Set<TrajectoryFitness> initialPopulation;

    private int populationSize;
    private int maxDepth = -1;
    private boolean acceptTrajectoryAtMaxDepth;
    protected HashMap<BatchTransformationRule<?, ?>, Integer> priorities;
    private IObjective objective;

    private boolean isInterrupted = false;
    protected Logger logger = Logger.getLogger(IStrategy.class);

    public FixedPriorityInitialSelector() {
        priorities = new HashMap<BatchTransformationRule<?,?>, Integer>();
    }

    /**
     * 
     * @param rule
     * @param priority Lower is better.
     * @return
     */
    public FixedPriorityInitialSelector withRulePriority(BatchTransformationRule<?,?> rule, int priority) {
        priorities.put(rule, priority);
        return this;
    }

    public FixedPriorityInitialSelector withHardObjective(IObjective objective) {
        this.objective = objective;
        return this;
    }

    public FixedPriorityInitialSelector withMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public FixedPriorityInitialSelector alwaysAcceptTrajectoryIfMaxDepthReached() {
        this.acceptTrajectoryAtMaxDepth = true;
        return this;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
        initialPopulation = new HashSet<TrajectoryFitness>(populationSize);

        Preconditions.checkNotNull(objective, "Hard objective is missing for FixedPriorityInitialSelector.");
        Preconditions.checkState(objective.isHardObjective(), "Given objective is not hard objective for FixedPriorityInitialSelector.");

        for (BatchTransformationRule<?, ?> batchTransformationRule : context.getGlobalContext().getTransformations()) {
            if (!priorities.containsKey(batchTransformationRule)) {
                throw new IllegalStateException("Missing rule priority for FixedPriorityInitialSelector.");
            }
        }
        
        FixedPriorityConflictResolver fixedPriorityResolver = ConflictResolvers.createFixedPriorityResolver();
        for (Entry<BatchTransformationRule<?, ?>, Integer> entry : priorities.entrySet()) {
            fixedPriorityResolver.setPriority(entry.getKey().getRuleSpecification(), entry.getValue());
        }
        context.changeActivationOrdering(fixedPriorityResolver.createConflictSet());

        objective.init(context);
        
        logger.info("FixedPriorityInitialSelector inited.");
    }

    @Override
    public void explore() {

        while (!(isInterrupted || initialPopulation.size() >= populationSize)) {

            Double fitness = objective.getFitness(context);
            boolean hardObjectiveIsSatisfied = objective.satisifiesHardObjective(fitness);

            if (0 <= maxDepth && maxDepth <= context.getDepth() ) {
                if (hardObjectiveIsSatisfied || acceptTrajectoryAtMaxDepth) {
                    saveTrajectory();
                }
                dsm.undoUntilRoot();
                continue;
            } else if (hardObjectiveIsSatisfied) {
                saveTrajectory();
                dsm.undoUntilRoot();
                continue;
            }

            dsm.executeRandomActivationId();
        }
        context.changeActivationOrderingBack();
        logger.info("FixedPriorityInitialSelector finished.");
    }

    private void saveTrajectory() {
        TrajectoryWithStateFitness traj = new TrajectoryWithStateFitness(dsm.getTrajectoryInfo(), context.calculateFitness());
        initialPopulation.add(traj);
        logger.debug("Initial trajectory found: " + traj.toString());
    }

    @Override
    public void interruptStrategy() {
        isInterrupted = true;
    }

    @Override
    public Set<TrajectoryFitness> getInitialPopulation() {
        return initialPopulation;
    }

}
