/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - adaptation to VIATRA Query based engine
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.localsearch;

import java.util.Arrays;

/**
 * VariableType will be mapped to PatternVariable in the interpreted
 * engine.
 **/
public class MatchingKey {
    Object[] keys;
    
    public MatchingKey(Object[] keys) {
        this.keys = Arrays.copyOf(keys,keys.length);
    }
    
    public boolean equals(Object other) {
        if (other instanceof MatchingKey) {
            MatchingKey otherKey = (MatchingKey) other;
            return Arrays.equals(keys, otherKey.keys);
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return Arrays.hashCode(keys);
    }
    
    public String toString() {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            key.append("_");
            key.append(keys[i].toString());
        }
        return key.toString();
    }
    
    public Object[] toArray() {
    	return keys.clone();
    }
}
