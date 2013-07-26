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

import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchList;
import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchSet;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * A wrapper class for filter definitions. It is used to create filtered observable Sets for patterns. A filter
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
     * Returns an observable Set of pattern matches, taking account the existing filter rules for a pattern. If no
     * filtering rule is defined for the selected pattern, all matches are returned.
     * 
     * @param pattern
     * @param engine
     * @return
     */
    public ObservablePatternMatchSet<IPatternMatch> getObservableSet(Pattern pattern, RuleEngine engine) {
        @SuppressWarnings("unchecked")
        IQuerySpecification<IncQueryMatcher<IPatternMatch>> querySpecification = (IQuerySpecification<IncQueryMatcher<IPatternMatch>>) QuerySpecificationRegistry
                .getOrCreateQuerySpecification(pattern);

        ObservablePatternMatchSet<IPatternMatch> obsSet = isFiltered(pattern) ? new ObservablePatternMatchSet<IPatternMatch>(querySpecification,
                engine,
                getFilter(pattern)) : new ObservablePatternMatchSet<IPatternMatch>(querySpecification, engine);
        return obsSet;
    }
   

    
    /**
     * Returns an observable list of pattern matches, taking account the existing filter rules for a pattern. If no
     * filtering rule is defined for the selected pattern, all matches are returned.
     * 
     * @param pattern
     * @param engine
     * @return
     */
    public ObservablePatternMatchList<IPatternMatch> getObservableList(Pattern pattern, RuleEngine engine) {
        @SuppressWarnings("unchecked")
        IQuerySpecification<IncQueryMatcher<IPatternMatch>> querySpecification = (IQuerySpecification<IncQueryMatcher<IPatternMatch>>) QuerySpecificationRegistry
                .getOrCreateQuerySpecification(pattern);

        ObservablePatternMatchList<IPatternMatch> obslist = isFiltered(pattern) ? new ObservablePatternMatchList<IPatternMatch>(querySpecification,
                engine,
                getFilter(pattern)) : new ObservablePatternMatchList<IPatternMatch>(querySpecification, engine);
        return obslist;
    }
}

