/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.interfaces;

import java.util.List;

import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
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
    
    List<TrajectoryFitness> getInitialPopulation();
}
