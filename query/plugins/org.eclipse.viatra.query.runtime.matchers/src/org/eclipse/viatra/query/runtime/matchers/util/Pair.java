/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

/**
 * Utility class representing a pair of objects.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class Pair<First, Second> {
    public final First first;
    public final Second second;
    
    public Pair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }
    
}
