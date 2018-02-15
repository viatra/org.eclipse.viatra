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
package org.eclipse.viatra.addon.databinding.runtime.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryFilterSemantics;

/**
 * Builder API for observable pattern match collections (lists and sets). 
 * This builder can be used for setting up complex observable collections including
 * filtering, comparator or converter. Existing matchers or rule engines are supported 
 * together with configuration from query specification or VIATRA Query engine. 
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

    private IQuerySpecification<? extends ViatraQueryMatcher<M>> specification = null;
    private EventFilter<M> filter = null;
    private ViatraQueryEngine queryEngine = null;
    private RuleEngine ruleEngine = null;
    private Comparator<M> comparator = null;
    private Function<M, ?> converter = null;
    private ViatraQueryMatcher<M> matcher = null;

    /**
     * Creates a builder for configuring an observable collection
     * observing the match set of the given {@link IQuerySpecification} 
     * 
     * @param specification
     *            the {@link IQuerySpecification} used to create a matcher
     */
    public static <M extends IPatternMatch> ObservablePatternMatchCollectionBuilder<M> create(
            IQuerySpecification<? extends ViatraQueryMatcher<M>> specification) {
        ObservablePatternMatchCollectionBuilder<M> builder = new ObservablePatternMatchCollectionBuilder<M>(specification);
        return builder;
    }

    /**
     * Creates a builder for configuring an observable collection
     * observing the match set of the given {@link ViatraQueryMatcher}.
     * 
     * @param matcher
     *            the {@link ViatraQueryMatcher} to use as the source of the observable collection
     */
    public static <M extends IPatternMatch> ObservablePatternMatchCollectionBuilder<M> create(
            ViatraQueryMatcher<M> matcher) {
        ObservablePatternMatchCollectionBuilder<M> builder = new ObservablePatternMatchCollectionBuilder<M>(matcher);
        return builder;
    }

    protected ObservablePatternMatchCollectionBuilder(IQuerySpecification<? extends ViatraQueryMatcher<M>> specification) {
        this.specification = specification;
    }

    protected ObservablePatternMatchCollectionBuilder(ViatraQueryMatcher<M> matcher) {
        this.matcher = matcher;
        this.queryEngine = matcher.getEngine();
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
    public ObservablePatternMatchCollectionBuilder<M> setFilter(Collection<M> multifilters, ViatraQueryFilterSemantics semantics){
        this.filter = Rules.newMultiMatchFilter(multifilters, semantics);
        return this;
    }
    
    /**
     *  Sets the given {@link ViatraQueryEngine} to be used as the engine of the built observable.
     */
    public ObservablePatternMatchCollectionBuilder<M> setEngine(ViatraQueryEngine engine) {
        this.queryEngine = engine;
        this.ruleEngine = null;
        return this;
    }

    /**
     *  Sets the given {@link RuleEngine} to be used as the engine of the built observable.
     */
    public ObservablePatternMatchCollectionBuilder<M> setEngine(RuleEngine engine) {
        this.ruleEngine = engine;
        this.queryEngine = null;
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
     * @since 2.0
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
        Preconditions.checkState(!(ruleEngine == null && queryEngine == null), "(VIATRA Query or Rule) Engine not set!");
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
            collection.initialize(queryEngine);
        }
    }
    
}
