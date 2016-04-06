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

import java.util.Collection;

import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public interface IStopCondition {

    /**
     * Returns true if the exploration process has to stop.
     * @param survivedPopulation The survevied population.
     * @return True if the exploration process has to stop.
     */
    boolean checkStopCondition(Collection<TrajectoryFitness> survivedPopulation);

}
