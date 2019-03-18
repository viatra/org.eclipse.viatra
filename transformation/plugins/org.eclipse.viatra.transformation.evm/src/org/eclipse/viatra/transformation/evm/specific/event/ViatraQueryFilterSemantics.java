/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

/**
 * Semantics for VIATRA query match-based filtering.
 * @author istvanrath
 *
 */
public enum ViatraQueryFilterSemantics {
    /**
     * allows passthrough if the given (single) (partial) match is compatible with the event.
     */
    SINGLE,
    /**
     * allows passthrough if any of the given (partial) matches are compatible with the event.
     */
    OR,
    /**
     * allows passthrough only if all of the given (partial) matches are compatible with the event.
     */
    AND
    
}
