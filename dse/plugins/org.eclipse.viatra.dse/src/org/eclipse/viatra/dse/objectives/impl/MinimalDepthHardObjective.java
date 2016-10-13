/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.objectives.impl;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IObjective;

/**
 * This hard objective is fulfilled if the trajectory is longer than a predefined number. A minimum length if 0 means
 * all trajectory will be regarded as a solution.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class MinimalDepthHardObjective extends BaseObjective {

    private static final String DEFAULT_NAME = "MinimalDepthHardObjective";
    protected int minDepth;
    private ThreadContext context;

    public MinimalDepthHardObjective(int minDepth) {
        super(DEFAULT_NAME);
        this.minDepth = minDepth;
    }

    public MinimalDepthHardObjective(String name, int minDepth) {
        super(name);
        this.minDepth = minDepth;
    }

    @Override
    public void init(ThreadContext context) {
        super.init(context);
        this.context = context;
    }

    @Override
    public Double getFitness(ThreadContext context) {
        return 0d;
    }

    @Override
    public boolean isHardObjective() {
        return true;
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {
        return context.getDepth() <= minDepth;
    }

    @Override
    public IObjective createNew() {
        return new MinimalDepthHardObjective(name, minDepth);
    }

}
