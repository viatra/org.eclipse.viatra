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

package org.eclipse.viatra.query.runtime.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.internal.apiimpl.ViatraQueryEngineImpl;
import org.eclipse.viatra.query.runtime.internal.apiimpl.QueryResultWrapper;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Preconditions;

/**
 * Base implementation of ViatraQueryMatcher.
 *
 * @author Bergmann Gábor
 *
 * @param <Match>
 */
public abstract class BaseMatcher<Match extends IPatternMatch> extends QueryResultWrapper implements ViatraQueryMatcher<Match> {

    // FIELDS AND CONSTRUCTOR

    protected ViatraQueryEngine engine;
    protected IQuerySpecification<? extends BaseMatcher<Match>> querySpecification;

    public BaseMatcher(ViatraQueryEngine engine,
    		IQuerySpecification<? extends BaseMatcher<Match>> querySpecification)
            throws ViatraQueryException {
        super();
        this.engine = engine;
        ViatraQueryEngineImpl engineImpl = (ViatraQueryEngineImpl) engine;
        this.querySpecification = querySpecification;
        try {
			this.querySpecification.getInternalQueryRepresentation().ensureInitialized();
		} catch (QueryInitializationException e) {
			throw new ViatraQueryException(e);
		}
        this.backend = accessMatcher(engineImpl, querySpecification);
        engineImpl.reportMatcherInitialized(querySpecification, this);
    }

    // HELPERS

    private IQueryResultProvider accessMatcher(ViatraQueryEngineImpl engine, IQuerySpecification<? extends BaseMatcher<Match>> specification) throws ViatraQueryException {
        Preconditions.checkArgument(!specification.getInternalQueryRepresentation().getStatus().equals(PQueryStatus.ERROR), "Cannot load erroneous query specification " + specification.getFullyQualifiedName());
        Preconditions.checkArgument(!specification.getInternalQueryRepresentation().getStatus().equals(PQueryStatus.UNINITIALIZED), "Cannot load uninitialized query specification " + specification.getFullyQualifiedName());
        try {
            return engine.getResultProvider(specification);
        } catch (QueryProcessingException e) {
            throw new ViatraQueryException(e);
        }
    }

    // ARRAY-BASED INTERFACE

    /** Converts the array representation of a pattern match to an immutable Match object. */
    protected abstract Match arrayToMatch(Object[] parameters);
    /** Converts the array representation of a pattern match to a mutable Match object. */
    protected abstract Match arrayToMatchMutable(Object[] parameters);

    /** Converts the Match object of a pattern match to the array representation. */
    protected Object[] matchToArray(Match partialMatch) {
        return partialMatch.toArray();
    }
    // TODO make me public for performance reasons
    protected abstract Match tupleToMatch(Tuple t);

    private Object[] fEmptyArray;

    protected Object[] emptyArray() {
        if (fEmptyArray == null)
            fEmptyArray = new Object[getSpecification().getParameterNames().size()];
        return fEmptyArray;
    }

    // REFLECTION

    @Override
    public Integer getPositionOfParameter(String parameterName) {
        return getSpecification().getPositionOfParameter(parameterName);
    }

    @Override
    public List<String> getParameterNames() {
        return getSpecification().getParameterNames();
    }

    // BASE IMPLEMENTATION

    @Override
    public Collection<Match> getAllMatches() {
        return rawGetAllMatches(emptyArray());
    }

    /**
     * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
     *
     * @param parameters
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @pre size of input array must be equal to the number of parameters.
     * @return matches represented as a Match object.
     */
    protected Collection<Match> rawGetAllMatches(Object[] parameters) {
        Collection<? extends Tuple> m = backend.getAllMatches(parameters);
        Collection<Match> matches = new ArrayList<Match>();
        // clones the tuples into a match object to protect the Tuples from modifications outside of the ReteMatcher
        for (Tuple t : m)
            matches.add(tupleToMatch(t));
        return matches;
    }

    @Override
    public Collection<Match> getAllMatches(Match partialMatch) {
        return rawGetAllMatches(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public Match getOneArbitraryMatch() {
        return rawGetOneArbitraryMatch(emptyArray());
    }

    /**
     * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
     * Neither determinism nor randomness of selection is guaranteed.
     *
     * @param parameters
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @pre size of input array must be equal to the number of parameters.
     * @return a match represented as a Match object, or null if no match is found.
     */
    protected Match rawGetOneArbitraryMatch(Object[] parameters) {
        Tuple t = backend.getOneArbitraryMatch(parameters);
        if (t != null)
            return tupleToMatch(t);
        else
            return null;
    }

    @Override
    public Match getOneArbitraryMatch(Match partialMatch) {
        return rawGetOneArbitraryMatch(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    /**
     * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match, under
     * any possible substitution of the unspecified parameters.
     *
     * @param parameters
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @return true if the input is a valid (partial) match of the pattern.
     */
    protected boolean rawHasMatch(Object[] parameters) {
        return backend.countMatches(parameters) > 0;
    }

    @Override
    public boolean hasMatch(Match partialMatch) {
        return rawHasMatch(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public int countMatches() {
        return rawCountMatches(emptyArray());
    }

    /**
     * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
     *
     * @param parameters
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @pre size of input array must be equal to the number of parameters.
     * @return the number of pattern matches found.
     */
    protected int rawCountMatches(Object[] parameters) {
        return backend.countMatches(parameters);
    }

    @Override
    public int countMatches(Match partialMatch) {
        return rawCountMatches(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    /**
     * Executes the given processor on each match of the pattern that conforms to the given fixed values of some
     * parameters.
     *
     * @param parameters
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @pre size of input array must be equal to the number of parameters.
     * @param action
     *            the action that will process each pattern match.
     */
    protected void rawForEachMatch(Object[] parameters, IMatchProcessor<? super Match> processor) {
        Collection<? extends Tuple> m = backend.getAllMatches(parameters);
        // clones the tuples into match objects to protect the Tuples from modifications outside of the ReteMatcher
        for (Tuple t : m)
            processor.process(tupleToMatch(t));
    }

    @Override
    public void forEachMatch(IMatchProcessor<? super Match> processor) {
        rawForEachMatch(emptyArray(), processor);
    };

    @Override
    public void forEachMatch(Match match, IMatchProcessor<? super Match> processor) {
        rawForEachMatch(match.toArray(), processor);
    };

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public boolean forOneArbitraryMatch(IMatchProcessor<? super Match> processor) {
        return rawForOneArbitraryMatch(emptyArray(), processor);
    }

    @Override
    public boolean forOneArbitraryMatch(Match partialMatch, IMatchProcessor<? super Match> processor) {
        return rawForOneArbitraryMatch(partialMatch.toArray(), processor);
    };

    /**
     * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed
     * values of some parameters. Neither determinism nor randomness of selection is guaranteed.
     *
     * @param parameters
     *            array where each non-null element binds the corresponding pattern parameter to a fixed value.
     * @pre size of input array must be equal to the number of parameters.
     * @param processor
     *            the action that will process the selected match.
     * @return true if the pattern has at least one match with the given parameter values, false if the processor was
     *         not invoked
     */
    protected boolean rawForOneArbitraryMatch(Object[] parameters, IMatchProcessor<? super Match> processor) {
        Tuple t = backend.getOneArbitraryMatch(parameters);
        if (t != null) {
            processor.process(tupleToMatch(t));
            return true;
        } else {
            return false;
        }
    }

    // with input binding as pattern-specific parameters: not declared in interface


    @Override
    public Match newEmptyMatch() {
        return arrayToMatchMutable(new Object[getParameterNames().size()]);
    }

    @Override
    public Match newMatch(Object... parameters) {
        return arrayToMatch(parameters);
    }

    @Override
    public Set<Object> getAllValues(final String parameterName) {
        return rawGetAllValues(getPositionOfParameter(parameterName), emptyArray());
    }

    @Override
    public Set<Object> getAllValues(final String parameterName, Match partialMatch) {
        return rawGetAllValues(getPositionOfParameter(parameterName), partialMatch.toArray());
    };

    /**
     * Retrieve the set of values that occur in matches for the given parameterName, that conforms to the given fixed
     * values of some parameters.
     *
     * @param position
     *            position of the parameter for which values are returned
     * @param parameters
     *            a parameter array corresponding to a partial match of the pattern where each non-null field binds the
     *            corresponding pattern parameter to a fixed value.
     * @return the Set of all values in the given position, null if no parameter with the given position exists or if
     *         parameters[position] is set, empty set if there are no matches
     */
    protected Set<Object> rawGetAllValues(final int position, Object[] parameters) {
        if (position >= 0 && position < getParameterNames().size()) {
            if (parameters.length == getParameterNames().size()) {
                if (parameters[position] == null) {
                    final Set<Object> results = new HashSet<Object>();
                    rawAccumulateAllValues(position, parameters, results);
                    return results;
                }
            }
        }
        return null;
    }

    /**
     * Uses an existing set to accumulate all values of the parameter with the given name. Since it is a protected
     * method, no error checking or input validation is performed!
     *
     * @param position
     *            position of the parameter for which values are returned
     * @param parameters
     *            a parameter array corresponding to a partial match of the pattern where each non-null field binds the
     *            corresponding pattern parameter to a fixed value.
     * @param accumulator
     *            the existing set to fill with the values
     */
    protected <T> void rawAccumulateAllValues(final int position, Object[] parameters, final Set<T> accumulator) {
        rawForEachMatch(parameters, new IMatchProcessor<Match>() {

            @SuppressWarnings("unchecked")
            @Override
            public void process(Match match) {
                accumulator.add((T) match.get(position));
            }
        });
    }

    @Override
    public ViatraQueryEngine getEngine() {
        return engine;
    }

	@Override
	public IQuerySpecification<? extends BaseMatcher<Match>> getSpecification() {
	    return querySpecification;
	}

	@Override
	public String getPatternName() {
	    return querySpecification.getFullyQualifiedName();
	}
}
