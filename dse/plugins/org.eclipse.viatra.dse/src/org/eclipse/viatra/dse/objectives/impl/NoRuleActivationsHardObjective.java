/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
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
 * This hard objective is satisfied if there are no rule activations from the current state (returning 1 in this case).
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class NoRuleActivationsHardObjective extends BaseObjective {

    protected static final String DEFAULT_NAME = "NoMoreActivationHardObjective";

    public NoRuleActivationsHardObjective(String name) {
        super(name);
    }

    public NoRuleActivationsHardObjective() {
        this(DEFAULT_NAME);
    }

    @Override
    public Double getFitness(ThreadContext context) {
        return context.getConflictSet().getNextActivations().isEmpty() ? 1d : 0d;
    }

    @Override
    public void init(ThreadContext context) {
    }

    @Override
    public IObjective createNew() {
        return this;
    }

    @Override
    public boolean isHardObjective() {
        return true;
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {
        return fitness.doubleValue() > 0.5d;
    }

}
