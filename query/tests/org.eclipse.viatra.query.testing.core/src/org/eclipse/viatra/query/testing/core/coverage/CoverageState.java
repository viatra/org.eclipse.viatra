/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
     * Element was removed during rewriting for some reason (e.g. an optimization)
     */
    NOT_REPRESENTED,
    
    /**
     * Element was removed for an unknown reason
     */
    NOT_REPRESENTED_UNKNOWN_REASON,
    
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
