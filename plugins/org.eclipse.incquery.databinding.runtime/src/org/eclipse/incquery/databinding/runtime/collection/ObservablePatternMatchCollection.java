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

import java.util.Comparator;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;

import com.google.common.base.Function;

/**
 * Internal interface for building observable pattern match collections.
 * The interface is implemented internally by both {@link ObservablePatternMatchList} and
 * {@link ObservablePatternMatchSet} so the {@link ObservablePatternMatchCollectionBuilder}
 * can work with both lists and sets.
 * 
 * @author Abel Hegedus
 *
 */
public interface ObservablePatternMatchCollection<M extends IPatternMatch> {

    /**
     * The updater is used for transforming match set changes to observable diffs.
     * 
     * @param converter used to transform matches to other type of objects
     * @param comparator used to order the content of lists
     */
    void createUpdater(Function<M, ? extends Object> converter, Comparator<M> comparator);
    
    /**
     * Creates the rule specification of the observable collection using a query specification.
     * 
     * @param querySpecification
     */
    void createRuleSpecification(IQuerySpecification<? extends IncQueryMatcher<M>> querySpecification);

    /**
     * Creates the rule specification of the observable collection using an existing matcher.
     * 
     * @param matcher
     */
    void createRuleSpecification(IncQueryMatcher<M> matcher);
    
    /**
     * Sets the filter used by the observable collection.
     * 
     * @param filter
     */
    void setFilter(EventFilter<M> filter);

    /**
     * Initializes the configured observable collection using an IncQuery engine.
     * 
     * @param engine
     */
    void initialize(IncQueryEngine engine);

    /**
     * Initializes the configured observable collection using an rule engine.
     * 
     * @param engine
     */
    void initialize(RuleEngine engine);
}
