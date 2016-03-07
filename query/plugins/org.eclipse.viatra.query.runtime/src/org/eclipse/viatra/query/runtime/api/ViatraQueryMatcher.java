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

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface for a VIATRA Query matcher associated with a graph pattern.
 * 
 * @param <Match>
 *            the IPatternMatch type representing a single match of this pattern.
 * @author Bergmann Gábor
 */
public interface ViatraQueryMatcher<Match extends IPatternMatch> {
    // REFLECTION
    /** The pattern that will be matched. */
    public abstract IQuerySpecification<? extends ViatraQueryMatcher<Match>> getSpecification();

    /** Fully qualified name of the pattern. */
    public abstract String getPatternName();

    /** Returns the index of the symbolic parameter with the given name. */
    public abstract Integer getPositionOfParameter(String parameterName);

    /** Returns the array of symbolic parameter names. */
    public abstract List<String> getParameterNames();

    // ALL MATCHES
    /**
     * Returns the set of all pattern matches.
     * 
     * @return matches represented as a Match object.
     */
    public abstract Collection<Match> getAllMatches();

    /**
     * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
     * 
     * @param partialMatch
     *            a partial match of the pattern where each non-null field binds the corresponding pattern parameter to
     *            a fixed value.
     * @return matches represented as a Match object.
     */
    public abstract Collection<Match> getAllMatches(Match partialMatch);

    // variant(s) with input binding as pattern-specific parameters: not declared in interface

    // SINGLE MATCH
    /**
     * Returns an arbitrarily chosen pattern match. Neither determinism nor randomness of selection is guaranteed.
     * 
     * @return a match represented as a Match object, or null if no match is found.
     */
    public abstract Match getOneArbitraryMatch();

    /**
     * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
     * Neither determinism nor randomness of selection is guaranteed.
     * 
     * @param partialMatch
     *            a partial match of the pattern where each non-null field binds the corresponding pattern parameter to
     *            a fixed value.
     * @return a match represented as a Match object, or null if no match is found.
     */
    public abstract Match getOneArbitraryMatch(Match partialMatch);

    // variant(s) with input binding as pattern-specific parameters: not declared in interface

    // MATCH CHECKING
    /**
     * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match, under
     * any possible substitution of the unspecified parameters (if any).
     * 
     * @param partialMatch
     *            a (partial) match of the pattern where each non-null field binds the corresponding pattern parameter
     *            to a fixed value.
     * @return true if the input is a valid (partial) match of the pattern.
     */
    public abstract boolean hasMatch(Match partialMatch);

    // variant(s) with input binding as pattern-specific parameters: not declared in interface

    // NUMBER OF MATCHES
    /**
     * Returns the number of all pattern matches.
     * 
     * @return the number of pattern matches found.
     */
    public abstract int countMatches();

    /**
     * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
     * 
     * @param partialMatch
     *            a partial match of the pattern where each non-null field binds the corresponding pattern parameter to
     *            a fixed value.
     * @return the number of pattern matches found.
     */
    public abstract int countMatches(Match partialMatch);

    // variant(s) with input binding as pattern-specific parameters: not declared in interface

    // FOR EACH MATCH
    /**
     * Executes the given processor on each match of the pattern.
     * 
     * @param processor
     *            the action that will process each pattern match.
     */
    public abstract void forEachMatch(IMatchProcessor<? super Match> processor);

    /**
     * Executes the given processor on each match of the pattern that conforms to the given fixed values of some
     * parameters.
     * 
     * @param partialMatch
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @param processor
     *            the action that will process each pattern match.
     */
    public abstract void forEachMatch(Match partialMatch, IMatchProcessor<? super Match> processor);

    // variant(s) with input binding as pattern-specific parameters: not declared in interface

    // FOR ONE ARBITRARY MATCH
    /**
     * Executes the given processor on an arbitrarily chosen match of the pattern. Neither determinism nor randomness of
     * selection is guaranteed.
     * 
     * @param processor
     *            the action that will process the selected match.
     * @return true if the pattern has at least one match, false if the processor was not invoked
     */
    public abstract boolean forOneArbitraryMatch(IMatchProcessor<? super Match> processor);

    /**
     * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed
     * values of some parameters. Neither determinism nor randomness of selection is guaranteed.
     * 
     * @param partialMatch
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @param processor
     *            the action that will process the selected match.
     * @return true if the pattern has at least one match with the given parameter values, false if the processor was
     *         not invoked
     */
    public abstract boolean forOneArbitraryMatch(Match partialMatch, IMatchProcessor<? super Match> processor);

    // variant(s) with input binding as pattern-specific parameters: not declared in interface

    // CHANGE MONITORING
    // attach delta monitor for high-level change detection
    
    // USE {@link IMatchUpdateListener} or EVM instead
	//    @Deprecated
	//	public abstract DeltaMonitor<Match> newDeltaMonitor(boolean fillAtStart);

    // USE {@link IMatchUpdateListener} or EVM instead
	//    @Deprecated
	//	public abstract DeltaMonitor<Match> newFilteredDeltaMonitor(boolean fillAtStart, Match partialMatch);

    /**
     * Returns an empty, mutable Match for the matcher. 
     * Fields of the mutable match can be filled to create a partial match, usable as matcher input. 
     * This can be used to call the matcher with a partial match 
     *  even if the specific class of the matcher or the match is unknown.
     * 
     * @return the empty match
     */
    public abstract Match newEmptyMatch();

    /**
     * Returns a new (partial) Match object for the matcher. 
     * This can be used e.g. to call the matcher with a partial
     * match. 
     * 
     * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
     * 
     * @param parameters
     *            the fixed value of pattern parameters, or null if not bound.
     * @return the (partial) match object.
     */
    public abstract Match newMatch(Object... parameters);

    /**
     * Retrieve the set of values that occur in matches for the given parameterName.
     * 
     * @param parameterName
     *            name of the parameter for which values are returned
     * @return the Set of all values for the given parameter, null if the parameter with the given name does not exists,
     *         empty set if there are no matches
     */
    public abstract Set<Object> getAllValues(final String parameterName);

    /**
     * Retrieve the set of values that occur in matches for the given parameterName, that conforms to the given fixed
     * values of some parameters.
     * 
     * @param parameterName
     *            name of the parameter for which values are returned
     * @param partialMatch
     *            a partial match of the pattern where each non-null field binds the corresponding pattern parameter to
     *            a fixed value.
     * @return the Set of all values for the given parameter, null if the parameter with the given name does not exists
     *         or if the parameter with the given name is set in partialMatch, empty set if there are no matches
     */
    public abstract Set<Object> getAllValues(final String parameterName, Match partialMatch);

    /**
     * Returns the engine that the matcher uses.
     * 
     * @return the engine
     */
    public abstract ViatraQueryEngine getEngine();
}