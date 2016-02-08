/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.event;

/**
 * Semantics for IncQuery match-based filtering.
 * @author istvanrath
 *
 */
public enum IncQueryFilterSemantics {
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
