/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.stopconditions;

import java.util.Collection;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.EvolutionaryStrategy;
import org.eclipse.viatra.dse.evolutionary.EvolutionaryStrategy.EvolutionaryStrategySharedObject;
import org.eclipse.viatra.dse.evolutionary.interfaces.IStopCondition;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class MaxEvaluationsStopCondition implements IStopCondition {

    private int maxEvaluations;
    private int evals = 0;
    private EvolutionaryStrategy.EvolutionaryStrategySharedObject so;
    
    public MaxEvaluationsStopCondition(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }
    
    @Override
    public void init(ThreadContext context) {
        so = (EvolutionaryStrategySharedObject) context.getSharedObject();
    }

    @Override
    public boolean checkStopCondition(Collection<TrajectoryFitness> survivedPopulation) {
        evals += so.childPopulationSize;
        return evals >= maxEvaluations;
    }

}
