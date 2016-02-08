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

import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * This is a generic pattern matcher for any IncQuery pattern, with "interpretative" query execution.
 * To use the pattern matcher on a given model, obtain a {@link GenericQuerySpecification} first, then 
 * invoke e.g. {@link GenericQuerySpecification#getMatcher(IncQueryEngine)}.
 * in conjunction with {@link IncQueryEngine#on(org.eclipse.incquery.runtime.api.scope.IncQueryScope)}.
 * <p>
 * Whenever available, consider using the pattern-specific generated matcher API instead.
 * 
 * <p>
 * Matches of the pattern will be represented as {@link GenericPatternMatch}.
 * 
 * @author Bergmann Gábor
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @see GenericQuerySpecification
 * @since 0.9
 */
public class GenericPatternMatcher extends BaseMatcher<GenericPatternMatch> {
	

    protected GenericPatternMatcher(
    		IncQueryEngine engine, 
    		GenericQuerySpecification<? extends GenericPatternMatcher> specification) 
    		throws IncQueryException 
    {
        super(engine, specification);
    }    

    @Override
    public GenericPatternMatch arrayToMatch(Object[] parameters) {
        return GenericPatternMatch.newMatch(getSpecification(), parameters);
    }
    
    @Override
    public GenericPatternMatch arrayToMatchMutable(Object[] parameters) {
        return GenericPatternMatch.newMutableMatch(getSpecification(), parameters);
    }

    @Override
    protected GenericPatternMatch tupleToMatch(Tuple t) {
        return new GenericPatternMatch.Immutable(getSpecification(), /*avoid re-cloning*/t.getElements());
    }
    
    @Override
    public GenericQuerySpecification<? extends GenericPatternMatcher> getSpecification() {
        return (GenericQuerySpecification<? extends GenericPatternMatcher>)querySpecification;
    }
    
    
    /**
     * Internal method for {@link GenericQuerySpecification}
     * @noreference
     */
	static <Matcher extends GenericPatternMatcher> GenericPatternMatcher instantiate(IncQueryEngine engine, GenericQuerySpecification<Matcher> querySpecification) throws IncQueryException {
		// check if matcher already exists
		GenericPatternMatcher matcher = engine.getExistingMatcher(querySpecification);
        if (matcher == null) {
        	matcher = new GenericPatternMatcher(engine, querySpecification);
        	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
        } 	
        return matcher;
	}

}
