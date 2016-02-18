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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueryHeader;

/**
 * API interface for an VIATRA query specification. Each query is associated with a pattern. Methods instantiate a matcher
 * of the pattern with various parameters.
 * 
 * <p> As of 0.9.0, some internal details (mostly relevant for query evaluator backends) have been moved to {@link #getInternalQueryRepresentation()}.  
 *
 * @author Bergmann Gábor
 *
 */
public interface IQuerySpecification<Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> extends PQueryHeader {

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
     * The matcher will be created within the managed {@link ViatraQueryEngine} belonging to the EMF model root, so
     * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
     *
     * @deprecated use {@link #getMatcher(ViatraQueryEngine)} instead, e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}.
     *
     * @param emfRoot
     *            the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.
     * @throws ViatraQueryException
     *             if an error occurs during pattern matcher creation
     */
    @Deprecated
    public Matcher getMatcher(Notifier emfRoot) throws ViatraQueryException;

    /**
     * Initializes the pattern matcher within an existing {@link ViatraQueryEngine}. If the pattern matcher is already
     * constructed in the engine, only a lightweight reference is created.
     * <p>
     * The match set will be incrementally refreshed upon updates.
     *
     * @param engine
     *            the existing VIATRA Query engine in which this matcher will be created.
     * @throws ViatraQueryException
     *             if an error occurs during pattern matcher creation
     */
    public Matcher getMatcher(ViatraQueryEngine engine) throws ViatraQueryException;

    
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
    
    /**
     * The query is formulated over this kind of modeling platform.
     * E.g. for queries over EMF models, the {@link EMFScope} class is returned.
     */
    public Class<? extends QueryScope> getPreferredScopeClass();
    
    /**
     * Returns the definition of the query in a format intended for consumption by the query evaluator. 
     * @return the internal representation of the query.
     */
    public PQuery getInternalQueryRepresentation();
    
}
