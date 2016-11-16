/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.stopconditions;

import java.util.Collection;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IStopCondition;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class IterationStopCondition implements IStopCondition {

    private int iterations;

    /**
     * 
     * @param iterations
     *            Number of iterations to make. If <= 1, then it will stop the exploration process after the initial
     *            population is generated.
     */
    public IterationStopCondition(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public void init(ThreadContext context) {
    }

    @Override
    public boolean checkStopCondition(Collection<TrajectoryFitness> survivedPopulation) {
        iterations--;
        return iterations <= 0;
    }

}
