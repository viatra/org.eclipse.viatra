/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.planner;


/**
 * Expresses the state of a PConstraint application 
 * condition with respect to a given adornment.
 * 
 * @author Marton Bur
 *
 */
public enum PConstraintCategory {
    /*
     * During plan creation an operation is considered a past 
     * operation, if an already bound variable is free in the
     * mask of the operation.
     * (Mask of the operation: the required binding state of 
     * the affected variables)
     */
    PAST, 
    /*
     * The binding states of the variables in the operation 
     * mask correspond to the current binding states of the 
     * variables in the search plan
     */
    PRESENT, 
    /*
     * There is at least one bound variable in the mask of 
     * a future operation that is still free at the current 
     * state of the plan. Also, a future operation can't be 
     * PAST.
     */
    FUTURE;
}
