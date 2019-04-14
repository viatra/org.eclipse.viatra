/**
 * Copyright (c) 2004-2019, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.runtime.emf.filters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

/**
 * A EVM filter that uses a parameter-predicate map that can be used for multiple patterns and rules.
 * </p>
 * 
 * This implementation is a generalization of the {@linkplain MatchParameterFilter} implementation by supporting more
 * complex conditions for each parameters. When only simple Object comparison is used, it is better to rely on the
 * previously available {@linkplain MatchParameterFilter} as it is easier to use and has a bit better performance.</p>
 * 
 * <b>Note</b> When using the filter, parameter keys that does not match any parameter of a pattern match are ignored.
 * 
 * @since 2.2
 * @see MatchParameterFilter
 */
public class MatchParameterPredicateFilter implements EventFilter<IPatternMatch> {
    private Map<String, Predicate<Object>> filterMap;

    public MatchParameterPredicateFilter(String name, Predicate<Object> parameterMatcher) {
        this.filterMap = new HashMap<>();
        filterMap.put(name, parameterMatcher);
    }
    
    public MatchParameterPredicateFilter(final Map<String, Predicate<Object>> parameterMatchers) {
        this.filterMap = new HashMap<>();
        this.filterMap.putAll(filterMap);
    }

    
    @SafeVarargs
    public MatchParameterPredicateFilter(final Entry<String, Predicate<Object>>... parameters) {
        this.filterMap = Arrays.stream(parameters).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @Override
    public boolean isProcessable(final IPatternMatch eventAtom) {
        return eventAtom.parameterNames().stream()
                .noneMatch(it -> filterMap.containsKey(it) && !filterMap.get(it).test(eventAtom.get(it)));
    }

}
