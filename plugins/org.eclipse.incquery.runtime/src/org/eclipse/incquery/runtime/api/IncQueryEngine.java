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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * A EMF-IncQuery incremental evaluation engine, attached to a model such as an EMF resource. The engine hosts pattern matchers, and
 * will listen on EMF update notifications stemming from the given model in order to maintain live results.
 * 
 * <p>
 * Pattern matchers within this engine may be instantiated in the following ways:
 * <ul>
 * <li>Recommended: instantiate the specific matcher class generated for the pattern by e.g. MyPatternMatcher.on(engine).
 * <li>Use {@link #getMatcher(Pattern)} if the pattern-specific generated matcher API is not available.
 * <li>Advanced: use the query specification associated with the generated matcher class to achieve the same.
 * </ul>
 * Additionally, a group of patterns (see {@link IPatternGroup}) can be initialized together before usage; this improves
 * the performance of pattern matcher construction, unless the engine is in wildcard mode.
 * 
 * <p>
 * By default, IncQueryEngines do not need to be separately disposed; they will be garbage collected along with the model. 
 * Advanced users: see {@link IncQueryEngineManager} and {@link AdvancedIncQueryEngine} if you want fine control over the lifecycle of an engine.
 * 
 * 
 * @author Bergmann Gábor
 * 
 */
public abstract class IncQueryEngine {
    
    /**
     * Initialize a (managed) {@link IncQueryEngine} on a matcher scope specified by a scope root of type {@link Notifier}.
     * @param emfScopeRoot the scope in which matches supported by the engine should be registered
     * @return a (managed) {@link IncQueryEngine} instance
     * @throws IncQueryException on initialization errors.
     */
	public static IncQueryEngine on(Notifier emfScopeRoot) throws IncQueryException {
		return IncQueryEngineManager.getInstance().getIncQueryEngine(emfScopeRoot);
	}
	

    /**
     * Specifies whether the base index should be built in wildcard mode. See {@link NavigationHelper} for the
     * explanation of wildcard mode.
     * 
     * @param wildcardMode
     *            the wildcardMode to set
     * @throws IncQueryException
     *             if the base index could not be initialized
     * @throws IllegalStateException
     *             if baseIndex is already constructed in the opposite mode, since the mode can not be changed once
     *             applied
     */
	public abstract void setWildcardMode(boolean wildcardMode) throws IncQueryException;

    /**
     * Provides access to the internal base index component of the engine, responsible for keeping track of basic EMF
     * contents of the model.
     * 
     * @return the baseIndex the NavigationHelper maintaining the base index
     * @throws IncQueryException
     *             if the base index could not be constructed
     */
	public abstract NavigationHelper getBaseIndex() throws IncQueryException;

	/**
	 * Access a pattern matcher for a given {@link Pattern} specification.
	 * @param pattern a {@link Pattern} specification (EMF model) that describes an EMF-IncQuery graph pattern
	 * @return a pattern matcher corresponding to the specification
	 * @throws IncQueryException if the matcher could not be initialized
	 */
	public abstract IncQueryMatcher<? extends IPatternMatch> getMatcher(Pattern pattern) throws IncQueryException;
	
	/**
	 * Access a pattern matcher based on a (generated) {@link IQuerySpecification}.
	 * @param querySpecification a {@link IQuerySpecification} that describes an EMF-IncQuery query
	 * @return a pattern matcher corresponding to the specification
	 * @throws IncQueryException if the matcher could not be initialized
	 */
    public abstract <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(IQuerySpecification<Matcher> querySpecification) throws IncQueryException;

    /**
     * Access an existing pattern matcher based on a (generated) {@link IQuerySpecification}.
     * @param querySpecification a {@link IQuerySpecification} that describes an EMF-IncQuery query
     * @return a pattern matcher corresponding to the specification, <code>null</code> if a matcher does not exist yet.
     */
	public abstract <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(IQuerySpecification<Matcher> querySpecification);

    
    /**
     * Access a copy of available {@link IncQueryMatcher} pattern matchers.
     * @return a copy of the set of currently available pattern matchers registered on this engine instance
     */
	public abstract Set<? extends IncQueryMatcher<? extends IPatternMatch>> getCurrentMatchers();

    /**
     * @return the scope of pattern matching, i.e. the root of the EMF model tree that this engine is attached to.
     */
	public abstract Notifier getScope();
	
    /**
     * Run-time events (such as exceptions during expression evaluation) will be logged to this logger.
     * <p>
     * DEFAULT BEHAVIOUR: If Eclipse is running, the default logger pipes to the Eclipse Error Log. Otherwise, messages
     * are written to stderr.
     * </p>
     * 
     * @return the logger that errors will be logged to during runtime execution.
     */
	public abstract Logger getLogger();


 
}
