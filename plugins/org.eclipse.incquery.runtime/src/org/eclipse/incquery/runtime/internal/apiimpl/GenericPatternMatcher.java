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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.GenericMatchProcessor;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * This is a generic pattern matcher for any EMF-IncQuery pattern, with "interpretative" query execution.
 * Use the pattern matcher on a given model via {@link #on(IncQueryEngine, Pattern)}, 
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
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
 */
public class GenericPatternMatcher extends BaseMatcher<GenericPatternMatch> {
	
    /**
     * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
     * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned. 
     * The match set will be incrementally refreshed upon updates.
     * 
     * @param engine
     *            the existing EMF-IncQuery engine in which this matcher will be created.
     * @param pattern
     *            the EMF-IncQuery pattern for which the matcher is to be constructed.
     * @throws IncQueryException
     *             if an error occurs during pattern matcher creation
     */
	public static GenericPatternMatcher on(IncQueryEngine engine, Pattern pattern) throws IncQueryException {
		return on(engine, new GenericQuerySpecification(pattern));
	}
	
    /**
     * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
     * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned. 
     * The match set will be incrementally refreshed upon updates.
     * 
     * @param engine
     *            the existing EMF-IncQuery engine in which this matcher will be created.
     * @param querySpecification
     *            the query specification for which the matcher is to be constructed.
     * @throws IncQueryException
     *             if an error occurs during pattern matcher creation
     */
	public static GenericPatternMatcher on(IncQueryEngine engine, GenericQuerySpecification querySpecification) throws IncQueryException {
		// check if matcher already exists
        GenericPatternMatcher matcher = engine.getExistingMatcher(querySpecification);
        if (matcher == null) {
        	matcher = new GenericPatternMatcher(engine, querySpecification);
        	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
        } 	
        return matcher;
	}
    /**
     * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
     * If a pattern matcher is already constructed with the same root, only a light-weight reference is returned.
     * 
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set will be incrementally refreshed upon updates from this scope.
     * 
     * <p>
     * The matcher will be created within the managed {@link IncQueryEngine} belonging to the EMF model root, so
     * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
     * 
     * @param pattern
     *            the EMF-IncQuery pattern for which the matcher is to be constructed.
     * @param emfRoot
     *            the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended:
     *            Resource or ResourceSet.
     * @throws IncQueryException
     *             if an error occurs during pattern matcher creation
	 * @deprecated use {@link #on(Pattern, IncQueryEngine)} instead, e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}
	 * 
	 */
	@Deprecated
    public GenericPatternMatcher(Pattern pattern, Notifier emfRoot) throws IncQueryException {
        this(pattern, IncQueryEngine.on(emfRoot));
    }

    /**
     * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
     * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned. 
     * The match set will be incrementally refreshed upon updates.
     * 
     * @param pattern
     *            the EMF-IncQuery pattern for which the matcher is to be constructed.
     * @param engine
     *            the existing EMF-IncQuery engine in which this matcher will be created.
     * @throws IncQueryException
     *             if an error occurs during pattern matcher creation
	 * @deprecated use {@link #on(Pattern, IncQueryEngine)} instead.
	 * 
	 */
	@Deprecated
    public GenericPatternMatcher(Pattern pattern, IncQueryEngine engine) throws IncQueryException {
        this(engine, new GenericQuerySpecification(pattern));
    }

    private GenericPatternMatcher(IncQueryEngine engine, GenericQuerySpecification specification) throws IncQueryException {
        super(engine, specification);
    }    

    @Override
    public GenericPatternMatch arrayToMatch(Object[] parameters) {
        return new GenericPatternMatch.Immutable(this, Arrays.copyOf(parameters, parameters.length));
    }
    
    @Override
    public GenericPatternMatch arrayToMatchMutable(Object[] parameters) {
        return new GenericPatternMatch.Mutable(this, parameters);
    }

    @Override
    protected GenericPatternMatch tupleToMatch(Tuple t) {
        return new GenericPatternMatch.Immutable(this, t.getElements());
    }

}
