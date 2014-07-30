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
package org.eclipse.viatra.dse.genetic.initialselectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.guidance.RuleInfo;

import com.google.common.collect.Lists;

public class FixedPrioritySelector implements IInitialPopulationSelector {

    private DesignSpaceManager dsm;

    private int initialSizeOfPopulation;
    private int foundInstances = 0;
    private Map<Object, Double> bestPriorityInState = new HashMap<Object, Double>();

    private IStoreChild store;

    private Random random = new Random();

    private List<PatternWithCardinality> goals;

    private Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos;

    private IncQueryEngine incqueryEngine;
    
    private Logger logger = Logger.getLogger(this.getClass());

    public FixedPrioritySelector(List<PatternWithCardinality> goals) {
        this.goals = goals;
    }

    @Override
    public void setChildStore(IStoreChild store) {
        this.store = store;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        initialSizeOfPopulation = populationSize;
    }

    @Override
    public void init(ThreadContext context) {
        if (store == null) {
            throw new DSEException("No IStoreChild is set for the BFSSelector");
        }
        dsm = context.getDesignSpaceManager();
        ruleInfos = context.getGuidance().getRuleInfos();
        incqueryEngine = context.getIncqueryEngine();
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful) {

        boolean isGoalState = true;
        for (PatternWithCardinality goal : goals) {
            if (!goal.isPatternSatisfied(incqueryEngine)) {
                isGoalState = false;
            }
        }
        
        if (isGoalState) {
            foundInstances++;
            logger.debug("Found goal state " + foundInstances + "/" + initialSizeOfPopulation);
            store.addChild(context);
            if (foundInstances >= initialSizeOfPopulation) {
                return null;
            }
            else {
                while(dsm.undoLastTransformation());
            }
        }
        
        Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
        while (transitions == null || transitions.isEmpty()) {
            if (!dsm.undoLastTransformation()) {
                return null;
            }

            transitions = dsm.getTransitionsFromCurrentState();
        }

        do {

            Double bestPriority = bestPriorityInState.get(dsm.getCurrentState().getId());
            if (bestPriority == null) {
                bestPriority = getBestPriority(dsm.getTransitionsFromCurrentState(), ruleInfos);
                bestPriorityInState.put(dsm.getCurrentState().getId(), bestPriority);
            }
            List<ITransition> bestTrasitions = Lists.newArrayList();
            for (ITransition iTransition : dsm.getTransitionsFromCurrentState()) {
                if (ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority() == bestPriority) {
                    bestTrasitions.add(iTransition);
                }
            }

            if (!bestTrasitions.isEmpty()) {
                int index = random.nextInt(bestTrasitions.size());
                ITransition iTransition = bestTrasitions.get(index);
                logger.debug("Transition fired: " + iTransition.getId() + " from " + iTransition.getFiredFrom().getId());
                
                if (iTransition.isAssignedToFire()) {
                    dsm.fireActivation(iTransition);
                    return getNextTransition(context, true);
                }
                
                return iTransition;
            }

            logger.debug("Backtrack");
            
        } while (!dsm.undoLastTransformation());

        return null;
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, boolean isGoalState,
            boolean constraintsNotSatisfied) {
        if (constraintsNotSatisfied) {
            dsm.undoLastTransformation();
        }
    }

    private double getBestPriority(Collection<? extends ITransition> transitions,
            Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos) {
        double bestPriority;
        bestPriority = Double.MIN_VALUE;
        for (ITransition iTransition : transitions) {
            double priority = ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority();
            if (priority > bestPriority) {
                bestPriority = priority;
            }
        }
        return bestPriority;
    }

}
