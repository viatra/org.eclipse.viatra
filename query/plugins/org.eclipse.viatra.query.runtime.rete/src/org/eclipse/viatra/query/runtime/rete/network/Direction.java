/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.network;

/**
 * Indicates whether a propagated update event signals the insertion or deletion of an element
 * 
 * @author Gabor Bergmann
 * 
 */
public enum Direction {
    INSERT, REVOKE;

    public Direction opposite() {
        switch (this) {
        case INSERT:
            return REVOKE;
        case REVOKE:
            return INSERT;
        default:
            return INSERT;
        }
    }
}
