/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.api;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;

/**
 * Generic interface for a single match of a pattern. Each instance is a (partial) substitution of pattern parameters,
 * essentially a parameter to value mapping.
 * 
 * Can also represent a partial match; unsubstituted parameters are assigned to null. Pattern matchers must never return
 * a partial match, but they accept partial matches as method parameters.
 * 
 * @author Bergmann Gábor
 */
public interface IPatternMatch extends Cloneable /* , Map<String, Object> */{
    /** @return the pattern for which this is a match. */
    public Pattern pattern();

    /** Identifies the name of the pattern for which this is a match. */
    public String patternName();

    /** Returns the list of symbolic parameter names. */
    public String[] parameterNames();

    /** Returns the value of the parameter with the given name, or null if name is invalid. */
    public Object get(String parameterName);

    /** Returns the value of the parameter at the given position, or null if position is invalid. */
    public Object get(int position);

    /**
     * Sets the parameter with the given name to the given value.
     * 
     * <p> Works only if match is mutable. See {@link #isMutable()}.
     * 
     * @returns true if successful, false if parameter name is invalid. May also fail and return false if the value type
     *          is incompatible.
     * @throws UnsupportedOperationException if match is not mutable.
     */
    public boolean set(String parameterName, Object newValue);

    /**
     * Sets the parameter at the given position to the given value.
     * 
     * <p> Works only if match is mutable. See {@link #isMutable()}.
     * 
     * @returns true if successful, false if position is invalid. May also fail and return false if the value type is
     *          incompatible.
     * @throws UnsupportedOperationException if match is not mutable.
     */
    public boolean set(int position, Object newValue);

    /**
     * Returns whether the match object can be further modified after its creation. Setters work only if the match is mutable. 
     * 
     * Matches computed by the pattern matchers are not mutable, so that the match set cannot be modified externally. 
     * Partial matches used as matcher input, however, can be mutable; such match objects can be created using {@link IncQueryMatcher#newEmptyMatch()}. 
     * 
     * @return whether the match can be modified
     */
    public boolean isMutable();
    
    /** 
     * Converts the match to an array representation, with each pattern parameter at their respective position. 
     * In case of a partial match, unsubstituted parameters will be represented as null elements in the array. 
     *
     * @return a newly constructed array containing each parameter substitution of the match in order.
     */
    public Object[] toArray();

    /** Prints the list of parameter-value pairs. */
    public String prettyPrint();
}
