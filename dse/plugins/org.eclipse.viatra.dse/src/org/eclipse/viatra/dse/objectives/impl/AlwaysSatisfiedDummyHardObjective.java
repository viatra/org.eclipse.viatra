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
 * This hard objective is fulfilled in any circumstances. Use it if all states should be regarded as a valid solution.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class AlwaysSatisfiedDummyHardObjective extends BaseObjective {

    private static final String DEFAULT_NAME = "AlwaysSatisfiedDummyHardObjective";

    public AlwaysSatisfiedDummyHardObjective() {
        super(DEFAULT_NAME);
    }

    public AlwaysSatisfiedDummyHardObjective(String name) {
        super(name);
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
        return true;
    }

    @Override
    public IObjective createNew() {
        return this;
    }

}
