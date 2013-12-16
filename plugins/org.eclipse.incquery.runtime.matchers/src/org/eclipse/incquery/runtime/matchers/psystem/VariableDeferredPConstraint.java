/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.Set;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;

/**
 * A kind of deferred constraint that can only be checked when a set of deferring variables are all present in a plan.
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class VariableDeferredPConstraint extends DeferredPConstraint {
    /**
     * @param affectedVariables
     */
    public VariableDeferredPConstraint(PSystem pSystem,
            Set<PVariable> affectedVariables) {
        super(pSystem, affectedVariables);
    }

    public abstract Set<PVariable> getDeferringVariables();

    /**
     * Refine further if needed
     */
    @Override
    public boolean isReadyAt(SubPlan plan) {
        return plan.getVariablesIndex().keySet().containsAll(getDeferringVariables());
    }

}
