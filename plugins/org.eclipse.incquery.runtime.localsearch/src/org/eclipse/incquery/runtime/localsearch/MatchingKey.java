/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - adaptation to EMF-IncQuery based engine
 *******************************************************************************/

package org.eclipse.incquery.runtime.localsearch;

/**
 * VariableType will be mapped to PatternVariable in the interpreted
 * engine.
 **/
public class MatchingKey {
    Object[] keys;
    
    public MatchingKey(Object[] keys) {
        this.keys = keys;
    }
    
    public boolean equals(Object other) {
        if (other != null && other instanceof MatchingKey) {
            MatchingKey otherKey = (MatchingKey) other;
            for (int i = 0; i < keys.length; i++) {
                if (! keys[i].equals(otherKey.keys[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return toString().hashCode();
    }
    
    public String toString() {
        StringBuffer key = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            key.append("_" + keys[i].toString()); 
        }
        return key.toString();
    }
    
    public Object[] toArray() {
    	return keys;
    }
}
