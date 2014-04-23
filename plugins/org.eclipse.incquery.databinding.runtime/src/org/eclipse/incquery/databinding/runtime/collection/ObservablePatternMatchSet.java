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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.runtime.Assert;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Observable view of a match set for a given {@link IncQueryMatcher} on a model (match sets of an
 * {@link IncQueryMatcher} are not ordered by default).
 * 
 * <p>
 * For creating complex observable lists, use {@link ObservablePatternMatchCollectionBuilder}.
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
    private SetCollectionUpdate updater;
    private RuleSpecification<Match> specification;
    private EventFilter<Match> matchFilter;
    private RuleEngine ruleEngine;

    private ObservablePatternMatchCollection<Match> internalCollection;
  
    /**
     * Creates an observable set, that will be built be the {@link ObservablePatternMatchCollectionBuilder}
     * using the {@link ObservablePatternMatchCollection} interface.
     */
    protected ObservablePatternMatchSet() {
        this.internalCollection = new ObservablePatternMatchCollection<Match>() {
                @Override
                public void createUpdater(Function<Match, ?> converter, Comparator<Match> comparator) {
                    updater = new SetCollectionUpdate(converter);
                }

                @Override
                public void createRuleSpecification(IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification) {
                    specification = ObservableCollectionHelper.createRuleSpecification(updater, querySpecification);
                }

                @Override
                public void createRuleSpecification(IncQueryMatcher<Match> matcher) {
                    specification = ObservableCollectionHelper.createRuleSpecification(updater, matcher);
                }

                @Override
                public void setFilter(EventFilter<Match> filter) {
                    matchFilter = filter;
                }

                @Override
                public void initialize(IncQueryEngine engine) {
                    ruleEngine = ObservableCollectionHelper.prepareRuleEngine(engine, specification, matchFilter);
                }

                @Override
                public void initialize(RuleEngine engine) {
                    ruleEngine = engine;
                    ruleEngine.addRule(specification, matchFilter);
                    ObservableCollectionHelper.fireActivations(engine, specification, matchFilter);
                }
                
            };
    }
    
    protected ObservablePatternMatchCollection<Match> getInternalCollection() {
        return internalCollection;
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
        protected final Function<Match, ?> converter;
        protected final Map<Match, Object> matchToItem;
        
        public SetCollectionUpdate(Function<Match, ?> converter) {
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
