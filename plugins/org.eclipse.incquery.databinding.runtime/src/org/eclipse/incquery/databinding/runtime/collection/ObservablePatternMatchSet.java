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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.runtime.Assert;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryFilterSemantics;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.base.Function;
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

    private final Set<Object> cache = Collections.synchronizedSet(new HashSet<Object>());
    private final SetCollectionUpdate updater;
    private RuleSpecification<Match> specification;
    private EventFilter<Match> matchFilter;

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
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, specification.createEmptyFilter());
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}. The given converter function is used on each match and the end result is put into the view.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created * @param converter the {@link Function}
     *            that is executed on each match to create the items in the list
     * @throws IncQueryException
     *             if the {@link IncQueryEngine} base index is not available
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(
            IQuerySpecification<Matcher> querySpecification, IncQueryEngine engine, Function<Match, Object> converter) {
        this(querySpecification, converter);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, specification.createEmptyFilter());
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
        matchFilter = Rules.newSingleMatchFilter(filter);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, matchFilter);
    }

    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification,
            IncQueryEngine engine, Collection<Match> multifilters, IncQueryFilterSemantics semantics) {
        this(querySpecification);
        matchFilter = Rules.newMultiMatchFilter(multifilters, semantics);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, matchFilter);
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
        engine.addRule(specification);
        ObservableCollectionHelper.fireActivations(engine, specification, specification.createEmptyFilter());
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
        matchFilter = Rules.newSingleMatchFilter(filter);
		engine.addRule(specification, matchFilter);
		ObservableCollectionHelper.fireActivations(engine, specification, matchFilter);
    }

    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification,
            RuleEngine engine, Collection<Match> multifilters, IncQueryFilterSemantics semantics) {
        this(querySpecification);
        matchFilter = Rules.newMultiMatchFilter(multifilters, semantics);
        engine.addRule(specification, matchFilter);
        ObservableCollectionHelper.fireActivations(engine, specification, matchFilter);
    }
    
    
    
    protected <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification, Function<Match, Object> converter) {
        super();
        updater = new SetCollectionUpdate(converter);
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, querySpecification);
    }

    protected <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IQuerySpecification<Matcher> querySpecification) {
        super();
        updater = new SetCollectionUpdate(null);
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
        updater = new SetCollectionUpdate(null);
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, matcher);
        ObservableCollectionHelper.prepareRuleEngine(matcher.getEngine(), specification, null);
    }
    
    @Override
    public Object getElementType() {
        if(updater.converter != null) {
            return Object.class;
        }
        return IPatternMatch.class;
    }

    @Override
    protected Set<Object> getWrappedSet() {
        return cache;
    }

    /**
     * @return the specification
     */
    public RuleSpecification<Match> getSpecification() {
        return specification;
    }

    public class SetCollectionUpdate implements IObservablePatternMatchCollectionUpdate<Match>{
        
        private static final String DATA_BINDING_REALM_MUST_NOT_BE_NULL = "Data binding Realm must not be null";
        protected final Function<Match, Object> converter;
        protected final Map<Match, Object> matchToItem;
        
        public SetCollectionUpdate(Function<Match, Object> converter) {
            if(converter != null) {
                this.converter = converter;
                matchToItem = new HashMap<Match, Object>();
            } else {
                this.converter = null;
                matchToItem = null;
            }
        }
        
        @Override
        public void addMatch(Match match) {
            Object item = match;
            if(converter != null) {
                item = converter.apply(match);
                matchToItem.put(match, item);
            }
            
            cache.add(item);
            final SetDiff diff = Diffs.createSetDiff(Sets.newHashSet(item), Collections.EMPTY_SET);
            Realm realm = getRealm();
            Assert.isNotNull(realm, DATA_BINDING_REALM_MUST_NOT_BE_NULL);
			realm.exec(new Runnable() {

				@Override
				public void run() {
				    if (!isDisposed()) {
				        fireSetChange(diff);
				    }
				}
			});
        }
    
        @Override
        public void removeMatch(Match match) {
            Object item = match;
            if(converter != null) {
                item = matchToItem.get(match);
            }
            
            cache.remove(item);
            final SetDiff diff = Diffs.createSetDiff(Collections.EMPTY_SET, Sets.newHashSet(item));
            Realm realm = getRealm();
            Assert.isNotNull(realm, DATA_BINDING_REALM_MUST_NOT_BE_NULL);
			realm.exec(new Runnable() {

				@Override
				public void run() {
				    if (!isDisposed()) {
				        fireSetChange(diff);
				    }
				}
			});
        }
    
    }

}
