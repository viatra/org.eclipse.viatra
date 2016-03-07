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

import java.util.Comparator;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

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
    void createRuleSpecification(IQuerySpecification<? extends ViatraQueryMatcher<M>> querySpecification);

    /**
     * Creates the rule specification of the observable collection using an existing matcher.
     * 
     * @param matcher
     */
    void createRuleSpecification(ViatraQueryMatcher<M> matcher);
    
    /**
     * Sets the filter used by the observable collection.
     * 
     * @param filter
     */
    void setFilter(EventFilter<M> filter);

    /**
     * Initializes the configured observable collection using a VIATRA Query engine.
     * 
     * @param engine
     */
    void initialize(ViatraQueryEngine engine);

    /**
     * Initializes the configured observable collection using an rule engine.
     * 
     * @param engine
     */
    void initialize(RuleEngine engine);
}
