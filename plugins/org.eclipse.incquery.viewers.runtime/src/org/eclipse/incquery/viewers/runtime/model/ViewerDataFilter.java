/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;
import java.util.Map;

import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchCollectionBuilder;
import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchList;
import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchSet;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryFilterSemantics;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * <p>A wrapper class for filter definitions. It is used to create filtered observable Sets for patterns. A filter
 * definition is considered unchangeable for the entire lifecycle of the ViewerFilter instance.</p>
 * 
 * <p>
 * <strong>Warning</strong> After using a filter to create a {@link ViewerDataFilter}, the updates of this filter will
 * not be pushed to the Filter</p>
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ViewerDataFilter {

    public static ViewerDataFilter UNFILTERED = new ViewerDataFilter();

    private Map<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>, ViewerFilterDefinition> filterDefinitions;
    
    /**
     * Initializes an empty data filter.
     */
    public ViewerDataFilter() {
        filterDefinitions = Maps.newHashMap();
    }

    /**
     * Initializes a data filter with a set of Pattern-IPatternMatch pairs.
     * 
     * @param filters
     */
    private ViewerDataFilter(Map<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>, ViewerFilterDefinition> filters) {
        filterDefinitions = Maps.newHashMap(filters);
    }

    /**
     * Copies all filter rules from an existing {@link ViewerDataFilter} instance.
     * 
     * @param other
     * @return
     */
    public static ViewerDataFilter cloneFilter(ViewerDataFilter other) {
        return new ViewerDataFilter(other.filterDefinitions);
    }

    /**
     * Adds a new filter to a viewer data filter.
     * 
     * @param pattern
     * @param match
     */
    public void addSingleFilter(IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> pattern, IPatternMatch match) {
        Preconditions.checkArgument(!filterDefinitions.containsKey(pattern), "Filter already defined for pattern "
                + pattern.getFullyQualifiedName());
        filterDefinitions.put(pattern, new ViewerFilterDefinition(pattern, 
                IncQueryFilterSemantics.SINGLE, 
                match, 
                null));
    }
    
    public void addMultiFilter(IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> pattern, Collection<IPatternMatch> matches, IncQueryFilterSemantics semantics) {
        Preconditions.checkArgument(!filterDefinitions.containsKey(pattern), "Filter already defined for pattern "
                + pattern.getFullyQualifiedName());
        filterDefinitions.put(pattern, new ViewerFilterDefinition(pattern, 
                semantics, 
                null, 
                matches));
    }

    /**
     * Removes a filter from the rules.
     * 
     * @param pattern
     */
    public void removeFilter(IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> pattern) {
        Preconditions.checkArgument(filterDefinitions.containsKey(pattern),
                "Filter undefined for pattern " + pattern.getFullyQualifiedName());
        filterDefinitions.remove(pattern);
    }

    public boolean isFiltered(IQuerySpecification<?> pattern) {
        return filterDefinitions.containsKey(pattern);
    }

    public ViewerFilterDefinition getFilter(IQuerySpecification<?> pattern) {
        return filterDefinitions.get(pattern);
    }

    /**
     * Returns an observable Set of pattern matches, taking account the existing filter rules for a pattern. If no
     * filtering rule is defined for the selected pattern, all matches are returned.
     * 
     * @param pattern
     * @param engine
     * @return
     */
    public <Match extends IPatternMatch> ObservablePatternMatchSet<Match> getObservableSet(IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, RuleEngine engine) {
        ObservablePatternMatchCollectionBuilder<Match> builder = ObservablePatternMatchCollectionBuilder.create(querySpecification).setEngine(engine);
        prepareFilter(querySpecification, builder);
        return builder.buildSet();
    }

    /**
     * Returns an observable list of pattern matches, taking account the existing filter rules for a pattern. If no
     * filtering rule is defined for the selected pattern, all matches are returned.
     * 
     * @param pattern
     * @param engine
     * @return
     */
    public <Match extends IPatternMatch> ObservablePatternMatchList<Match> getObservableList(IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, RuleEngine engine) {
        ObservablePatternMatchCollectionBuilder<Match> builder = ObservablePatternMatchCollectionBuilder.create(querySpecification).setEngine(engine);
        prepareFilter(querySpecification, builder);
        return builder.buildList();
    }

    private <Match extends IPatternMatch> void prepareFilter(
            IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification,
            ObservablePatternMatchCollectionBuilder<Match> builder) {
        if (isFiltered(querySpecification)) {
            ViewerFilterDefinition def = getFilter(querySpecification);
            switch (def.semantics) {
            case SINGLE:
                @SuppressWarnings("unchecked")
                Match singleFilterMatch = (Match) def.singleFilterMatch;
                builder.setFilter(singleFilterMatch);
                break;
            default:
                @SuppressWarnings("unchecked")
                Collection<Match> filterMatches = (Collection<Match>)def.filterMatches;
                builder.setFilter(filterMatches, def.semantics);
                break;
            }
        }
    }
}

