/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryFilterSemantics;

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

    private static final String FILTER_DEFINED_FOR_PATTERN_MSG = "Filter already defined for pattern %s";

    public static final ViewerDataFilter UNFILTERED = new ViewerDataFilter();

    private Map<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>, ViewerFilterDefinition> filterDefinitions;
    
    /**
     * Initializes an empty data filter.
     */
    public ViewerDataFilter() {
        filterDefinitions = new HashMap<>();
    }

    /**
     * Initializes a data filter with a set of Pattern-IPatternMatch pairs.
     * 
     * @param filters
     */
    private ViewerDataFilter(Map<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>, ViewerFilterDefinition> filters) {
        filterDefinitions = new HashMap<>(filters);
    }

    /**
     * Copies all filter rules from an existing {@link ViewerDataFilter} instance.
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
    public void addSingleFilter(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern, IPatternMatch match) {
        Preconditions.checkArgument(!filterDefinitions.containsKey(pattern), FILTER_DEFINED_FOR_PATTERN_MSG,
                pattern.getFullyQualifiedName());
        filterDefinitions.put(pattern, new ViewerFilterDefinition(pattern, 
                ViatraQueryFilterSemantics.SINGLE, 
                match, 
                null));
    }
    
    public void addMultiFilter(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern, Collection<IPatternMatch> matches, ViatraQueryFilterSemantics semantics) {
        Preconditions.checkArgument(!filterDefinitions.containsKey(pattern), FILTER_DEFINED_FOR_PATTERN_MSG,
                pattern.getFullyQualifiedName());
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
    public void removeFilter(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern) {
        Preconditions.checkArgument(filterDefinitions.containsKey(pattern),
                "Filter undefined for pattern %s", pattern.getFullyQualifiedName());
        filterDefinitions.remove(pattern);
    }

    public boolean isFiltered(IQuerySpecification<?> pattern) {
        return filterDefinitions.containsKey(pattern);
    }

    public ViewerFilterDefinition getFilter(IQuerySpecification<?> pattern) {
        return filterDefinitions.get(pattern);
    }

}

