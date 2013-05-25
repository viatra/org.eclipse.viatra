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

import java.util.Map;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * A wrapper class for filter definitions. It is used to create filtered observable lists for patterns. A filter
 * definition is considered unchangeable</p>
 * 
 * <p>
 * <strong>Warning</strong> After using a filter to create a {@link ViewerDataFilter}, the updates of this filter will
 * not be pushed to the Filter
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ViewerDataFilter {

    public static ViewerDataFilter UNFILTERED = new ViewerDataFilter();

    private Map<Pattern, IPatternMatch> filterDefinitions;

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
    public ViewerDataFilter(Map<Pattern, IPatternMatch> filters) {
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
    public void addFilter(Pattern pattern, IPatternMatch match) {
        Preconditions.checkArgument(!filterDefinitions.containsKey(pattern), "Filter already defined for pattern "
                + pattern.getName());
        filterDefinitions.put(pattern, match);
    }

    /**
     * Removes a filter from the rules.
     * 
     * @param pattern
     */
    public void removeFilter(Pattern pattern) {
        Preconditions.checkArgument(filterDefinitions.containsKey(pattern),
                "Filter undefined for pattern " + pattern.getName());
        filterDefinitions.remove(pattern);
    }

    public boolean isFiltered(Pattern pattern) {
        return filterDefinitions.containsKey(pattern);
    }

    public IPatternMatch getFilter(Pattern pattern) {
        return filterDefinitions.get(pattern);
    }

    /**
     * Returns an observable list of pattern matches, taking account the existing filter rules for a pattern. If no
     * filtering rule is defined for the selected pattern, all matches are returned.
     * 
     * @param pattern
     * @param engine
     * @return
     */
    public IObservableList getObservableList(Pattern pattern, IncQueryEngine engine) {
        @SuppressWarnings("unchecked")
        IQuerySpecification<IncQueryMatcher<IPatternMatch>> querySpecification = (IQuerySpecification<IncQueryMatcher<IPatternMatch>>) QuerySpecificationRegistry
                .getOrCreateQuerySpecification(pattern);

        IObservableList obslist = isFiltered(pattern) ? IncQueryObservables.observeMatchesAsList(querySpecification,
                engine, getFilter(pattern)) : IncQueryObservables.observeMatchesAsList(querySpecification, engine);
        return obslist;
    }
}
