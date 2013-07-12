/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.collect.Sets;

/**
 * Observable view of a match set for a given {@link IncQueryMatcher} on a model (match sets of an
 * {@link IncQueryMatcher} are not ordered by default).
 * 
 * <p>
 * This implementation uses the {@link ExecutionSchema} to get notifications for match set changes, and can be instantiated
 * using either an existing {@link IncQueryMatcher}, or an {@link IQuerySpecification} and {@link IncQueryEngine} or {@link RuleEngine}.
 * 
 * @author Abel Hegedus
 * 
 */
public class ObservablePatternMatchSet<Match extends IPatternMatch> extends AbstractObservableSet {

    private final Set<Match> cache = Collections.synchronizedSet(new HashSet<Match>());
    private final SetCollectionUpdate updater = new SetCollectionUpdate();
    private RuleSpecification<Match> specification;

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created
     * @throws IncQueryException if the {@link IncQueryEngine} base index is not available
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification,
            IncQueryEngine engine) {
        this(querySpecification);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, null);
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created
     * @param filter the partial match to be used as filter
     * @throws IncQueryException if the {@link IncQueryEngine} base index is not available
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification,
            IncQueryEngine engine, Match filter) {
        this(querySpecification);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, filter);
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            an existing {@link ExecutionSchema} that specifies the used model
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification,
            RuleEngine engine) {
        this(querySpecification);
        engine.addRule(specification, true);
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            an existing {@link ExecutionSchema} that specifies the used model
     * @param filter the partial match to be used as filter
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification,
            RuleEngine engine, Match filter) {
        this(querySpecification);
        engine.addRule(specification, true, specification.createFilter(filter));
    }

    protected <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification) {
        super();
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, querySpecification);
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IncQueryMatcher}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param matcher
     *            the {@link IncQueryMatcher} to use as the source of the observable set
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(Matcher matcher) {
        super();
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, matcher);
        ObservableCollectionHelper.prepareRuleEngine(matcher.getEngine(), specification, null);
    }
    
    @Override
    public Object getElementType() {
        return IPatternMatch.class;
    }

    @Override
    protected Set<Match> getWrappedSet() {
        return cache;
    }

    /**
     * @return the specification
     */
    protected RuleSpecification<Match> getSpecification() {
        return specification;
    }

    public class SetCollectionUpdate implements IObservablePatternMatchCollectionUpdate<Match>{
        
        @SuppressWarnings("unchecked")
        @Override
        public void addMatch(Match match) {
            cache.add(match);
            SetDiff diff = Diffs.createSetDiff(Sets.newHashSet(match), Collections.EMPTY_SET);
            fireSetChange(diff);
        }
    
        @SuppressWarnings("unchecked")
        @Override
        public void removeMatch(Match match) {
            cache.remove(match);
            SetDiff diff = Diffs.createSetDiff(Collections.EMPTY_SET, Sets.newHashSet(match));
            fireSetChange(diff);
        }
    
    }

}
