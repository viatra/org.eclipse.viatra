/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.interfaces;

import java.util.List;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public interface IEvolutionaryStrategyAdapter {

    void init(ThreadContext context);
    void iterationCompleted(List<TrajectoryFitness> currentPopulation, List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation, List<TrajectoryFitness> survivedPopulation, boolean stop);
    
}
