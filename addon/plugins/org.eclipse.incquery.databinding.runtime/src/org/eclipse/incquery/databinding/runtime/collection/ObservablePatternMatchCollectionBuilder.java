/*******************************************************************************
 * Copyright (c) 2010-2014, Abel Hegedus, Istvan Rath and Daniel Varro
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
import java.util.Comparator;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryFilterSemantics;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Builder API for observable pattern match collections (lists and sets). 
 * This builder can be used for setting up complex observable collections including
 * filtering, comparator or converter. Existing matchers or rule engines are supported 
 * together with configuration from query specification or IncQuery engine. 
 * 
 * <ul>
 * <li>
 * Use the {@link #create} methods to initialize a builder with a given matcher or query
 * specification. 
 * <li>
 * Use the set methods ({@link #setEngine},{@link #setFilter}, {@link #setComparator}, {@link #setConverter})
 * to configure the builder.
 * <li>
 * Finally, use the {@link #buildList()} and {@link #buildSet()} methods to create an observable
 * list or set as required.
 * </ul>
 * 
 * The same builder can be used to build multiple observable collections and the configuration
 * can be updated as needed. Each built observable will use the configuration available when the
 * build method is invoked. 
 * 
 * @author Abel Hegedus
 *
 */
public class ObservablePatternMatchCollectionBuilder<M extends IPatternMatch> {

    private IQuerySpecification<? extends IncQueryMatcher<M>> specification = null;
    private EventFilter<M> filter = null;
    private IncQueryEngine incQueryEngine = null;
    private RuleEngine ruleEngine = null;
    private Comparator<M> comparator = null;
    private Function<M, ?> converter = null;
    private IncQueryMatcher<M> matcher = null;

    /**
     * Creates a builder for configuring an observable collection
     * observing the match set of the given {@link IQuerySpecification} 
     * 
     * @param specification
     *            the {@link IQuerySpecification} used to create a matcher
     */
    public static <M extends IPatternMatch> ObservablePatternMatchCollectionBuilder<M> create(
            IQuerySpecification<? extends IncQueryMatcher<M>> specification) {
        ObservablePatternMatchCollectionBuilder<M> builder = new ObservablePatternMatchCollectionBuilder<M>(specification);
        return builder;
    }

    /**
     * Creates a builder for configuring an observable collection
     * observing the match set of the given {@link IncQueryMatcher}.
     * 
     * @param matcher
     *            the {@link IncQueryMatcher} to use as the source of the observable collection
     */
    public static <M extends IPatternMatch> ObservablePatternMatchCollectionBuilder<M> create(
            IncQueryMatcher<M> matcher) {
        ObservablePatternMatchCollectionBuilder<M> builder = new ObservablePatternMatchCollectionBuilder<M>(matcher);
        return builder;
    }

    protected ObservablePatternMatchCollectionBuilder(IQuerySpecification<? extends IncQueryMatcher<M>> specification) {
        this.specification = specification;
    }

    protected ObservablePatternMatchCollectionBuilder(IncQueryMatcher<M> matcher) {
        this.matcher = matcher;
        this.incQueryEngine = matcher.getEngine();
    }
    
    /**
     * Sets the event filter used by the built observable collection.
     */
    public ObservablePatternMatchCollectionBuilder<M> setFilter(EventFilter<M> filter){
        this.filter = filter;
        return this;
    }

    /**
     * Sets the given (partial) match as an event filter used by the built observable collection.
     */
    public ObservablePatternMatchCollectionBuilder<M> setFilter(M filter){
        this.filter = Rules.newSingleMatchFilter(filter);
        return this;
    }
    
    /**
     * Sets the given collection of (partial) matches as an event filter with the given semantics
     *  used by the built observable collection.
     */
    public ObservablePatternMatchCollectionBuilder<M> setFilter(Collection<M> multifilters, IncQueryFilterSemantics semantics){
        this.filter = Rules.newMultiMatchFilter(multifilters, semantics);
        return this;
    }
    
    /**
     *  Sets the given {@link IncQueryEngine} to be used as the engine of the built observable.
     */
    public ObservablePatternMatchCollectionBuilder<M> setEngine(IncQueryEngine engine) {
        this.incQueryEngine = engine;
        this.ruleEngine = null;
        return this;
    }

    /**
     *  Sets the given {@link RuleEngine} to be used as the engine of the built observable.
     */
    public ObservablePatternMatchCollectionBuilder<M> setEngine(RuleEngine engine) {
        this.ruleEngine = engine;
        this.incQueryEngine = null;
        return this;
    }
    
    /**
     * The given comparator is used to define the ordering between the elements in the built observable.
     */
    public ObservablePatternMatchCollectionBuilder<M> setComparator(Comparator<M> comparator){
        this.comparator = comparator;
        return this;
    }
    
    /**
     * The given converter function is used on each match and the end result is put into the
     * observable collection.
     */
    public ObservablePatternMatchCollectionBuilder<M> setConverter(Function<M, ?> converter){
        this.converter = converter;
        return this;
    }
    
    /**
     * Builds an {@link ObservablePatternMatchList} based on the configuration set in the builder.
     */
    public ObservablePatternMatchList<M> buildList() {
        checkBuilderConfiguration();
        ObservablePatternMatchList<M> observable = new ObservablePatternMatchList<M>();
        ObservablePatternMatchCollection<M> collection = observable.getInternalCollection();
        buildCollection(collection);
        return observable;
    }

    /**
     * Builds an {@link ObservablePatternMatchSet} based on the configuration set in the builder.
     */
    public ObservablePatternMatchSet<M> buildSet() {
        checkBuilderConfiguration();
        Preconditions.checkState(comparator == null, "Cannot use comparator in set!");
        ObservablePatternMatchSet<M> observable = new ObservablePatternMatchSet<M>();
        ObservablePatternMatchCollection<M> collection = observable.getInternalCollection();
        buildCollection(collection);
        return observable;
    }

    private void checkBuilderConfiguration() {
        Preconditions.checkState(!(ruleEngine == null && incQueryEngine == null), "(IncQuery or Rule) Engine not set!");
        Preconditions.checkState(specification != null || matcher != null, "Matcher or QuerySpecification not set!");
    }

    private void buildCollection(ObservablePatternMatchCollection<M> collection) {
        // create updater
        collection.createUpdater(converter, comparator);
        // create rule specification
        if(specification == null) {
            collection.createRuleSpecification(matcher);
        } else {
            collection.createRuleSpecification(specification);
        }
        collection.setFilter(filter);
        
        if(ruleEngine != null) {
            collection.initialize(ruleEngine);
        } else {
            collection.initialize(incQueryEngine);
        }
    }
    
}
