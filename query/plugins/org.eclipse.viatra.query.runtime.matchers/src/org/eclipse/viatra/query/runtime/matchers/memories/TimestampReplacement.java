/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, itemis AG, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.memories;

/**
 * Represents a replacement between timestamps. 
 * Either the old or the new timestamp can be null, but not at the same time. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class TimestampReplacement<Timestamp extends Comparable<Timestamp>> {

    public final Timestamp oldValue;
    public final Timestamp newValue;
    
    public TimestampReplacement(final Timestamp oldValue, final Timestamp newValue) {
        if (oldValue == null && newValue == null) {
            throw new IllegalArgumentException("Old and new cannot be both null at the same time!");
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
}
