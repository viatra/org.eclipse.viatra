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

package org.eclipse.incquery.runtime.internal.apiimpl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;

/**
 * Generic signature object implementation. Please instantiate using {@link GenericPatternMatcher#newMatch(Object...)} or {@link GenericPatternMatcher#newEmptyMatch()}.
 * 
 * See also the generated matcher and signature of the pattern, with pattern-specific API simplifications.
 * 
 * @author Bergmann Gábor
 * 
 */
public abstract class GenericPatternMatch extends BasePatternMatch {

    private final GenericPatternMatcher matcher;
    private final Object[] array;

    /**
     * @param posMapping
     * @param array
     */
    private GenericPatternMatch(GenericPatternMatcher matcher, Object[] array) {
        super();
        this.matcher = matcher;
        this.array = array;
    }

    @Override
    public Object get(String parameterName) {
        Integer index = matcher.getPositionOfParameter(parameterName);
        return index == null ? null : array[index];
    }

    @Override
    public boolean set(String parameterName, Object newValue) {
    	if (!isMutable()) throw new UnsupportedOperationException();
        Integer index = matcher.getPositionOfParameter(parameterName);
        if (index == null)
            return false;
        array[index] = newValue;
        return true;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(array, array.length);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < array.length; ++i)
            result = prime * result + ((array[i] == null) ? 0 : array[i].hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof GenericPatternMatch)) { // this should be infrequent
	        if (obj == null)
	            return false;
	        if (!(obj instanceof IPatternMatch))
	            return false;
	        IPatternMatch other = (IPatternMatch) obj;
	        if (!pattern().equals(other.pattern()))
	            return false;
	        return Arrays.deepEquals(array, other.toArray());	     	
        }
    	final GenericPatternMatch other = (GenericPatternMatch) obj;
		return pattern().equals(other.pattern()) && Arrays.deepEquals(array, other.array);
    }

    @Override
    public String prettyPrint() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0)
                result.append(", ");
            result.append("\"" + parameterNames().get(i) + "\"=" + prettyPrintValue(array[i]));
        }
        return result.toString();
    }

    @Override
    public Pattern pattern() {
        return matcher.getPattern();
    }

    @Override
    public String patternName() {
        return matcher.getPatternName();
    }

    @Override
    public List<String> parameterNames() {
        return matcher.getParameterNames();
    }

    
    static final class Mutable extends GenericPatternMatch {

		Mutable(GenericPatternMatcher matcher, Object[] array) {
			super(matcher, array);
		}
    	
		@Override
		public boolean isMutable() {
			return true;
		}
    }
    static final class Immutable extends GenericPatternMatch {

    	Immutable(GenericPatternMatcher matcher, Object[] array) {
			super(matcher, array);
		}
    	
		@Override
		public boolean isMutable() {
			return false;
		}
    }
}
