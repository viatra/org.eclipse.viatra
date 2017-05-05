/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.planning;

import org.eclipse.viatra.query.runtime.matchers.planning.operations.POperation;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;

/**
 * Single entry point for creating subplans.
 * Can be subclassed by query planner to provide specialized SubPlans.
 * @author Bergmann Gabor
 *
 */
public class SubPlanFactory {

    protected PBody body;

    public SubPlanFactory(PBody body) {
        super();
        this.body = body;
    }
    
    public SubPlan createSubPlan(POperation operation, SubPlan... parentPlans) {
        return new SubPlan(body, operation, parentPlans);		
    }
    
}
