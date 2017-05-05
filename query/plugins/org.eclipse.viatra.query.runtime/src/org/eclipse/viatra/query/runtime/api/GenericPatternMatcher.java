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

package org.eclipse.viatra.query.runtime.api;

import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * This is a generic pattern matcher for any VIATRA pattern, with "interpretative" query execution.
 * To use the pattern matcher on a given model, obtain a {@link GenericQuerySpecification} first, then 
 * invoke e.g. {@link GenericQuerySpecification#getMatcher(ViatraQueryEngine)}.
 * in conjunction with {@link ViatraQueryEngine#on(org.eclipse.viatra.query.runtime.api.scope.QueryScope)}.
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
    
    /**
     * @deprecated use {@link #GenericPatternMatcher(GenericQuerySpecification)} instead
     */
    @Deprecated
    protected GenericPatternMatcher(
            ViatraQueryEngine engine, 
            GenericQuerySpecification<? extends GenericPatternMatcher> specification) 
            throws ViatraQueryException 
    {
        super(engine, specification);
    }    
    
    /**
     * @since 1.4
     */
    public GenericPatternMatcher(GenericQuerySpecification<? extends GenericPatternMatcher> specification) 
            throws ViatraQueryException {
        super(specification);
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
    
    @SuppressWarnings("unchecked")
    @Override
    public GenericQuerySpecification<? extends GenericPatternMatcher> getSpecification() {
        return (GenericQuerySpecification<? extends GenericPatternMatcher>)querySpecification;
    }

    /**
     * Internal method for {@link GenericQuerySpecification}
     * @noreference
     */
    static <Matcher extends GenericPatternMatcher> GenericPatternMatcher instantiate(GenericQuerySpecification<Matcher> querySpecification) throws ViatraQueryException {
        return new GenericPatternMatcher(querySpecification);
    }
  
    /**
     * Internal method for {@link GenericQuerySpecification}
     * @noreference
     */
    static <Matcher extends GenericPatternMatcher> GenericPatternMatcher instantiate(ViatraQueryEngine engine, GenericQuerySpecification<Matcher> querySpecification) throws ViatraQueryException {
        // check if matcher already exists
        GenericPatternMatcher matcher = engine.getExistingMatcher(querySpecification);
        if (matcher == null) {
            matcher = engine.getMatcher(querySpecification);
        } 	
        return matcher;
    }

}
