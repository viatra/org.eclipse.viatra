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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
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

/**
 * Observable view of a match set for a given {@link IncQueryMatcher} on a model (match sets of an
 * {@link IncQueryMatcher} are ordered by the order of their appearance).
 * 
 * <p>
 * This implementation uses the {@link ExecutionSchema} to get notifications for match set changes, and can be
 * instantiated using either an existing {@link IncQueryMatcher}, or an {@link IQuerySpecification} and either a
 * {@link IncQueryEngine} or {@link ExecutionSchema}.
 * 
 * @author Abel Hegedus
 * 
 */
public class ObservablePatternMatchList<Match extends IPatternMatch> extends AbstractObservableList {

    private final List<Object> cache = Collections.synchronizedList(new ArrayList<Object>());
    private final ListCollectionUpdate updater;
    private RuleSpecification<Match> specification;
    
    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsList} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created
     * @throws IncQueryException if the {@link IncQueryEngine} base index is not available
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification,
            IncQueryEngine engine) {
        this(querySpecification);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, null);
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}. The given converter function is used on each match and the end result is put into the
     * view.
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created
     * @param converter
     *            the {@link Function} that is executed on each match to create the items in the list
     * @throws IncQueryException
     *             if the {@link IncQueryEngine} base index is not available
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(
            IQuerySpecification<Matcher> querySpecification, IncQueryEngine engine, Function<Match, Object> converter) {
        this(querySpecification, converter);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, null);
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsList} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created
     * @param filter the partial match to be used as filter
     * @throws IncQueryException if the {@link IncQueryEngine} base index is not available
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification,
            IncQueryEngine engine, Match filter) {
        this(querySpecification);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, filter);
    }

    
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification,
            IncQueryEngine engine, Collection<Match> multifilters, IncQueryFilterSemantics semantics) {
        this(querySpecification);
        ObservableCollectionHelper.prepareRuleEngine(engine, specification, multifilters, semantics);
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link RuleEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsList} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            an existing {@link RuleEngine} that specifies the used model
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification,
            RuleEngine engine) {
        this(querySpecification);
        engine.addRule(specification);
        ObservableCollectionHelper.fireActivations(engine, specification, specification.createEmptyFilter());
    }

    /**
     * Creates an observable view of the match set of the given {@link IQuerySpecification} initialized on the given
     * {@link RuleEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsList} instead!
     * 
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create a matcher
     * @param engine
     *            an existing {@link RuleEngine} that specifies the used model
     * @param filter the partial match to be used as filter
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification,
            RuleEngine engine, Match filter) {
        this(querySpecification);
        EventFilter<Match> matchFilter = Rules.newSingleMatchFilter(filter);
		engine.addRule(specification, matchFilter);
		ObservableCollectionHelper.fireActivations(engine, specification, matchFilter);
    }
    
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification,
            RuleEngine engine, Collection<Match> multifilter, IncQueryFilterSemantics semantics) {
        this(querySpecification);
        EventFilter<Match> matchFilter = Rules.newMultiMatchFilter(multifilter, semantics);
        engine.addRule(specification, matchFilter);
        ObservableCollectionHelper.fireActivations(engine, specification, matchFilter);
    }
    
    
    protected <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification, Function<Match, Object> converter) {
        super();
        updater = new ListCollectionUpdate(converter);
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, querySpecification);
    }

    protected <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification) {
        super();
        updater = new ListCollectionUpdate(null);
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, querySpecification);
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IncQueryMatcher}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsList} instead!
     * 
     * @param matcher
     *            the {@link IncQueryMatcher} to use as the source of the observable list
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(Matcher matcher) {
        super();
        updater = new ListCollectionUpdate(null);
        this.specification = ObservableCollectionHelper.createRuleSpecification(updater, matcher);
        ObservableCollectionHelper.prepareRuleEngine(matcher.getEngine(), specification, null);
    }
    
    @Override
    public Object getElementType() {
        return IPatternMatch.class;
    }

    @Override
    protected int doGetSize() {
        return cache.size();
    }

    @Override
    public Object get(int index) {
        return cache.get(index);
    }

    /**
     * @return the specification
     */
    public RuleSpecification<Match> getSpecification() {
        return specification;
    }

    public class ListCollectionUpdate implements IObservablePatternMatchCollectionUpdate<Match> {
        
        protected final Function<Match, Object> converter;
        protected final Map<Match, Object> matchToItem;
        
        /**
         * 
         */
        public ListCollectionUpdate(Function<Match, Object> converter) {
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
            
            ListDiffEntry diffentry = Diffs.createListDiffEntry(cache.size(), true, item);
            cache.add(item);
            final ListDiff diff = Diffs.createListDiff(diffentry);
            Realm realm = getRealm();
            Assert.isNotNull(realm, "Data binding Realm must not be null");
			realm.exec(new Runnable() {

				@Override
				public void run() {
				    if (!isDisposed()) {
				        fireListChange(diff);
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
            
            final int index = cache.indexOf(item);
            ListDiffEntry diffentry = Diffs.createListDiffEntry(index, false, item);
            cache.remove(item);
            final ListDiff diff = Diffs.createListDiff(diffentry);
            Realm realm = getRealm();
            Assert.isNotNull(realm, "Data binding Realm must not be null");
			realm.exec(new Runnable() {

				@Override
				public void run() {
				    if (!isDisposed()) {
				        fireListChange(diff);
				    }
				}
			});
        }
    }

}
