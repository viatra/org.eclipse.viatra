/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IEvolutionaryStrategyAdapter;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class EvolutionaryStrategyDebugAdapter implements IEvolutionaryStrategyAdapter {

    private int n = 0;
    private Logger logger = Logger.getLogger(IStrategy.class);

    public EvolutionaryStrategyDebugAdapter() {
    }

    @Override
    public void init(ThreadContext context) {
    }

    @Override
    public void iterationCompleted(List<TrajectoryFitness> currentPopulation,
            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation,
            List<TrajectoryFitness> survivedPopulation, boolean stop) {

        logger.debug("--------- Iteration " + n++ + "-----------");
        for (TrajectoryFitness traj : currentPopulation) {
            logger.debug(Integer.toString(traj.survive) + traj);
        }
    }

}
