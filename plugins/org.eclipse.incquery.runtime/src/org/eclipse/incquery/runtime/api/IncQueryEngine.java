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

import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * An IncQuery incremental evaluation engine, attached to a model such as an EMF resource. The engine hosts pattern matchers, and
 * will listen on model update notifications stemming from the given model in order to maintain live results.
 * 
 * <p>
 * By default, IncQueryEngines do not need to be separately disposed; they will be garbage collected along with the model. 
 * Advanced users: see {@link AdvancedIncQueryEngine} if you want fine control over the lifecycle of an engine.
 * 
 * <p>
 * Pattern matchers within this engine may be instantiated in the following ways:
 * <ul>
 * <li>Recommended: instantiate the specific matcher class generated for the pattern by e.g. MyPatternMatcher.on(engine).
 * <li>Use {@link #getMatcher(IQuerySpecification)} if the pattern-specific generated matcher API is not available.
 * <li>Advanced: use the query specification associated with the generated matcher class to achieve the same.
 * </ul>
 * Additionally, a group of patterns (see {@link IQueryGroup}) can be initialized together before usage; this may improve
 * the performance of pattern matcher construction by trying to gather all necessary information from the model in one go. 
 * Note that no such improvement is to be expected if the engine is specifically constructed in wildcard mode, 
 * an option available in some scope implementations  
 * (see {@link EMFScope#EMFScope(Notifier, BaseIndexOptions)} and {@link BaseIndexOptions#withWildcardMode(boolean)}).
 * 
 * 
 * @author Bergmann Gábor
 * 
 */
public abstract class IncQueryEngine {
    
    /**
     * Obtain a (managed) {@link IncQueryEngine} on a matcher scope specified by a scope root of type {@link Notifier}.
     * 
     * <p> For a given matcher scope, the same engine will be returned to any client. 
     * This facilitates the reuse of internal caches of the engine, greatly improving performance.  
     * 
     * <p> The lifecycle of this engine is centrally managed, and will not be disposed as long as the model is retained in memory. 
     * The engine will be garbage collected along with the model. 
     * 
     * <p>
     * Advanced users: see {@link AdvancedIncQueryEngine#createUnmanagedEngine(Notifier)} to obtain a private, 
     * 
     * unmanaged engine that is not shared with other clients and allows tight control over its lifecycle. 
     * 
     * @param emfScopeRoot the scope in which matches supported by the engine should be registered
     * @return a (managed) {@link IncQueryEngine} instance
     * @throws IncQueryException on initialization errors.
     * @deprecated use {@link #on(IncQueryScope)} instead to evaluate queries on both EMF and non-EMF scopes.
     */
	@Deprecated
	public static IncQueryEngine on(Notifier emfScopeRoot) throws IncQueryException {
		return IncQueryEngineManager.getInstance().getIncQueryEngine(new EMFScope(emfScopeRoot));
	}
    
    /**
     * Obtain a (managed) {@link IncQueryEngine} to evaluate queries over a given scope specified by an {@link IncQueryScope}.
     * 
     * <p> For a given matcher scope, the same engine will be returned to any client. 
     * This facilitates the reuse of internal caches of the engine, greatly improving performance.  
     * 
     * <p> The lifecycle of this engine is centrally managed, and will not be disposed as long as the model is retained in memory. 
     * The engine will be garbage collected along with the model. 
     * 
     * <p>
     * Advanced users: see {@link AdvancedIncQueryEngine#createUnmanagedEngine(IncQueryScope)} to obtain a private, 
     * unmanaged engine that is not shared with other clients and allows tight control over its lifecycle. 
     * 
     * @param scope 
     * 		the scope of query evaluation; the definition of the set of model elements that this engine is operates on. 
     * 		Provide e.g. a {@link EMFScope} for evaluating queries on an EMF model.
     * @return a (managed) {@link IncQueryEngine} instance
     * @throws IncQueryException on initialization errors.
     */
	public static IncQueryEngine on(IncQueryScope scope) throws IncQueryException {
		return IncQueryEngineManager.getInstance().getIncQueryEngine(scope);
	}

    /**
     * Provides access to the internal base index component of the engine, responsible for keeping track of basic EMF
     * contents of the model.
     * 
     * @return the baseIndex the NavigationHelper maintaining the base index
     * @throws IncQueryException
     *             if the base index could not be constructed
     */
	public abstract IBaseIndex getBaseIndex() throws IncQueryException;

	/**
	 * Access a pattern matcher based on a {@link IQuerySpecification}. 
	 * Multiple calls will return the same matcher.
	 * @param querySpecification a {@link IQuerySpecification} that describes an IncQuery query
	 * @return a pattern matcher corresponding to the specification
	 * @throws IncQueryException if the matcher could not be initialized
	 */
    public abstract <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(IQuerySpecification<Matcher> querySpecification) throws IncQueryException;

	/**
	 * Access a pattern matcher for the graph pattern with the given fully qualified name. 
	 * Will succeed only if a matcher for this pattern has already been constructed in this engine, 
	 *  or else if the matcher for the pattern has been generated and registered. 
	 * Multiple calls will return the same matcher. 
	 * 
	 * @param patternFQN the fully qualified name of an IncQuery graph pattern
	 * @return a pattern matcher corresponding to the specification
	 * @throws IncQueryException if the matcher could not be initialized
	 */
	public abstract IncQueryMatcher<? extends IPatternMatch> getMatcher(String patternFQN) throws IncQueryException;
    
    /**
     * Access an existing pattern matcher based on a {@link IQuerySpecification}.
     * @param querySpecification a {@link IQuerySpecification} that describes an IncQuery query
     * @return a pattern matcher corresponding to the specification, <code>null</code> if a matcher does not exist yet.
     */
	public abstract <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(IQuerySpecification<Matcher> querySpecification);

    
    /**
     * Access a copy of available {@link IncQueryMatcher} pattern matchers.
     * @return a copy of the set of currently available pattern matchers registered on this engine instance
     */
	public abstract Set<? extends IncQueryMatcher<? extends IPatternMatch>> getCurrentMatchers();
	
	public Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getRegisteredQuerySpecifications() {
	    return Sets.newHashSet(Collections2.transform(getCurrentMatchers(), new Function<IncQueryMatcher<?>, IQuerySpecification<?>>() {

            @Override
            public IQuerySpecification<?> apply(IncQueryMatcher<?> arg0) {
                return arg0.getSpecification();
            }
        }));
	}

    /**
     * @deprecated only valid for EMF scopes; use {@link #getScope()} to get scope information in the general case.
     * @return the scope of pattern matching, i.e. the root of the EMF model tree that this engine is attached to.
     */
	@Deprecated
	public abstract Notifier getEMFRoot();

    /**
     * @return the scope of query evaluation; the definition of the set of model elements that this engine is operates on.
     */
	public abstract IncQueryScope getScope();

}
