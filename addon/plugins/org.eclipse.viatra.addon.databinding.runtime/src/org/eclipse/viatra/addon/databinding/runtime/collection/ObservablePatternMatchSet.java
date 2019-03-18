/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.databinding.runtime.collection;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.runtime.Assert;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

/**
 * Observable view of a match set for a given {@link ViatraQueryMatcher} on a model (match sets of an
 * {@link ViatraQueryMatcher} are not ordered by default).
 * 
 * <p>
 * For creating complex observable lists, use {@link ObservablePatternMatchCollectionBuilder}.
 * 
 * <p>
 * This implementation uses the {@link ExecutionSchema} to get notifications for match set changes, and can be instantiated
 * using either an existing {@link ViatraQueryMatcher}, or an {@link IQuerySpecification} and {@link ViatraQueryEngine} or {@link RuleEngine}.
 * 
 * @author Abel Hegedus
 * 
 */
public class ObservablePatternMatchSet<Match extends IPatternMatch> extends AbstractObservableSet {

    private final Set<Object> cache = Collections.synchronizedSet(new HashSet<>());
    private SetCollectionUpdate updater;
    private RuleSpecification<Match> specification;
    private EventFilter<Match> matchFilter;
    private RuleEngine ruleEngine;
    private boolean privateRuleEngine;

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
                public void createRuleSpecification(IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification) {
                    specification = ObservableCollectionHelper.createRuleSpecification(updater, querySpecification);
                }

                @Override
                public void createRuleSpecification(ViatraQueryMatcher<Match> matcher) {
                    specification = ObservableCollectionHelper.createRuleSpecification(updater, matcher);
                }

                @Override
                public void setFilter(EventFilter<Match> filter) {
                    if(filter == null) {
                        matchFilter = specification.createEmptyFilter();
                    } else {
                        matchFilter = filter;
                    }
                }

                @Override
                public void initialize(ViatraQueryEngine engine) {
                    ruleEngine = ObservableCollectionHelper.prepareRuleEngine(engine, specification, matchFilter);
                    privateRuleEngine = true;
                }

                @Override
                public void initialize(RuleEngine engine) {
                    ruleEngine = engine;
                    privateRuleEngine = false;
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
            // XXX we cannot get the class of the generic parameter or the return type of the converter
            return null;
        }
        return IPatternMatch.class;
    }

    @Override
    protected Set<Object> getWrappedSet() {
        return cache;
    }
    
    @Override
    public synchronized void dispose() {
        if (ruleEngine != null) {
            ruleEngine.removeRule(specification, matchFilter);
            if (privateRuleEngine && ruleEngine.getRuleSpecificationMultimap().isEmpty()) {
                ObservableCollectionHelper.disposeRuleEngine(ruleEngine);
            }
            ruleEngine = null;
        }
        clear();
        super.dispose();
    }
    
    @Override
    public void clear() {
        this.cache.clear();
        this.updater.clear();
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
        protected final Map<Match, Object> matchToItem = new HashMap<Match, Object>();
        
        /**
         * @since 2.0
         */
        public SetCollectionUpdate(Function<Match, ?> converter) {
            if(converter != null) {
                this.converter = converter;
            } else {
                this.converter = null;
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
            final SetDiff diff = Diffs.createSetDiff(Collections.singleton(item), Collections.emptySet());
            Realm realm = getRealm();
            Assert.isNotNull(realm, DATA_BINDING_REALM_MUST_NOT_BE_NULL);
            realm.exec(() -> {
                if (!isDisposed()) {
                    fireSetChange(diff);
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
            final SetDiff diff = Diffs.createSetDiff(Collections.emptySet(), Collections.singleton(item));
            Realm realm = getRealm();
            Assert.isNotNull(realm, DATA_BINDING_REALM_MUST_NOT_BE_NULL);
            realm.exec(() -> {
                if (!isDisposed()) {
                    fireSetChange(diff);
                }
            });
        }

        @Override
        public void clear() {
            this.matchToItem.clear();
        }
    
    }

}
