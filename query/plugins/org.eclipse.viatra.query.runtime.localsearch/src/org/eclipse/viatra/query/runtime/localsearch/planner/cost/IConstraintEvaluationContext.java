/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner.cost;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import java.util.Collection;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;

/**
 * This interface denotes the evaluation context of a constraint, intended for cost estimation. Provides access to information
 * on which the cost function can base its calculation.
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public interface IConstraintEvaluationContext {

    /**
     * Get the constraint to be evaluated
     * @return
     */
    public PConstraint getConstraint();

    /**
     * Unbound variables at the time of evaluating the constraint
     * @return
     */
    public Collection<PVariable> getFreeVariables();

    /**
     * Bound variables at the time of evaluating the constraint
     * @return
     */
    public Collection<PVariable> getBoundVariables();
    
    public IQueryRuntimeContext getRuntimeContext();
    
}
