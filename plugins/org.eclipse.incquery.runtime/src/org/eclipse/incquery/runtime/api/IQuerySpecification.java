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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

/**
 * Interface for an IncQuery query specification. Each query is associated with a pattern. Methods instantiate a matcher
 * of the pattern with various parameters.
 *
 * @author Bergmann Gábor
 *
 */
public interface IQuerySpecification<Matcher extends IncQueryMatcher<? extends IPatternMatch>> extends PQuery {

    /**
     * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). If a pattern
     * matcher is already constructed with the same root, only a lightweight reference is created.
     *
     *
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * <p>
     * The match set will be incrementally refreshed upon updates from this scope.
     *
     * <p>
     * The matcher will be created within the managed {@link IncQueryEngine} belonging to the EMF model root, so
     * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
     *
     * @deprecated use {@link #getMatcher(IncQueryEngine)} instead, e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
     *
     * @param emfRoot
     *            the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.
     * @throws IncQueryException
     *             if an error occurs during pattern matcher creation
     */
    @Deprecated
    public Matcher getMatcher(Notifier emfRoot) throws IncQueryException;

    /**
     * Initializes the pattern matcher within an existing {@link IncQueryEngine}. If the pattern matcher is already
     * constructed in the engine, only a lightweight reference is created.
     * <p>
     * The match set will be incrementally refreshed upon updates.
     *
     * @param engine
     *            the existing EMF-IncQuery engine in which this matcher will be created.
     * @throws IncQueryException
     *             if an error occurs during pattern matcher creation
     */
    public Matcher getMatcher(IncQueryEngine engine) throws IncQueryException;

    
    /**
     * Returns an empty, mutable Match compatible with matchers of this query. 
     * Fields of the mutable match can be filled to create a partial match, usable as matcher input. 
     * This can be used to call the matcher with a partial match 
     *  even if the specific class of the matcher or the match is unknown.
     * 
     * @return the empty match
     */
    public abstract IPatternMatch newEmptyMatch();

    /**
     * Returns a new (partial) Match object compatible with matchers of this query. 
     * This can be used e.g. to call the matcher with a partial
     * match. 
     * 
     * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
     * 
     * @param parameters
     *            the fixed value of pattern parameters, or null if not bound.
     * @return the (partial) match object.
     */
    public abstract IPatternMatch newMatch(Object... parameters);
    
}
