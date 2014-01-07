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

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;

/**
 * Any constraint that can only be checked on certain SubPlans (e.g. those plans that already contain some variables).
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class DeferredPConstraint extends BasePConstraint {

    public DeferredPConstraint(PBody pSystem, Set<PVariable> affectedVariables) {
        super(pSystem, affectedVariables);
    }

    public abstract boolean isReadyAt(SubPlan plan, IPatternMatcherContext context);

}
