/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.coverage;

/**
 * Possible coverage states for a PSystem element.
 *
 * @since 1.6
 */
public enum CoverageState {

    /**
     * Element is covered
     */
    COVERED,
    
    /**
     * Element is not covered
     */
    NOT_COVERED,
    
    /**
     * Coverage cannot be determined for Element
     */
    UNDEFINED,
    
    /**
     * Element is not represented by the underlying engine, usually meaning it was
     * removed by an optimization.
     */
    NOT_REPRESENTED
    
    ;
    
    public CoverageState best(CoverageState other){
        if (other == null){
            return this;
        }
        if (this.ordinal() > other.ordinal()){
            return other;
        }
        return this;
    }
}
