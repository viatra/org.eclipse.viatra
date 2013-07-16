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
import java.util.Collections;
import java.util.List;

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
import org.eclipse.incquery.runtime.exception.IncQueryException;

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

    private final List<Match> cache = Collections.synchronizedList(new ArrayList<Match>());
    private final ListCollectionUpdate updater = new ListCollectionUpdate();
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
        engine.addRule(specification, true);
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
        engine.addRule(specification, true,specification.createFilter(filter));
    }
    
    protected <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IQuerySpecification<Matcher> querySpecification) {
        super();
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
    protected RuleSpecification<Match> getSpecification() {
        return specification;
    }

    public class ListCollectionUpdate implements IObservablePatternMatchCollectionUpdate<Match> {
        @Override
        public void addMatch(Match match) {
            ListDiffEntry diffentry = Diffs.createListDiffEntry(cache.size(), true, match);
            cache.add(match);
            final ListDiff diff = Diffs.createListDiff(diffentry);
            Realm realm = getRealm();
            Assert.isNotNull(realm, "Data binding Realm must not be null");
			realm.exec(new Runnable() {

				@Override
				public void run() {
					fireListChange(diff);
				}
			});
        }

        @Override
        public void removeMatch(Match match) {
            final int index = cache.indexOf(match);
            ListDiffEntry diffentry = Diffs.createListDiffEntry(index, false, match);
            cache.remove(match);
            final ListDiff diff = Diffs.createListDiff(diffentry);
            Realm realm = getRealm();
            Assert.isNotNull(realm, "Data binding Realm must not be null");
			realm.exec(new Runnable() {

				@Override
				public void run() {
					fireListChange(diff);
				}
			});
        }
    }

}
