/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.interfaces;

import java.util.Set;

import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public interface IInitialPopulationSelector extends IStrategy {

    /**
     * Sets the populations size in the initialization phase. The engine expects this amount of child from the
     * implementation of this interface via the {@link IStoreChild} implementation.
     * 
     * @param populationSize
     *            Expected number of children.
     */
    void setPopulationSize(int populationSize);
    
    Set<TrajectoryFitness> getInitialPopulation();
}
