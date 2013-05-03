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
import org.eclipse.incquery.runtime.api.impl.BaseQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * This is a generic query specification for EMF-IncQuery pattern matchers, for "interpretative" query execution. Instantiate the
 * specification with any registered pattern, and then use the specification to obtain an actual pattern matcher operating on a
 * given model.
 * 
 * <p>
 * When available, consider using the pattern-specific generated matcher API instead.
 * 
 * <p>
 * The created matcher will be of type GenericPatternMatcher. Matches of the pattern will be represented as
 * GenericPatternMatch.
 * 
 * @see GenericPatternMatcher
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @author Bergmann Gábor
 */
public class GenericQuerySpecification extends BaseQuerySpecification<GenericPatternMatcher> {
    public Pattern pattern;

    /**
     * Initializes a generic query specification for a given pattern.
     * 
     * @param patternName
     *            the name of the pattern for which matchers are to be constructed.
     */
    public GenericQuerySpecification(Pattern pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public GenericPatternMatcher instantiate(IncQueryEngine engine) throws IncQueryException {
        return new GenericPatternMatcher(engine, this);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
    	return (obj == this) || 
    			(obj instanceof GenericQuerySpecification && 
    					pattern.equals(((GenericQuerySpecification)obj).pattern));
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	return pattern.hashCode();
    }

}
