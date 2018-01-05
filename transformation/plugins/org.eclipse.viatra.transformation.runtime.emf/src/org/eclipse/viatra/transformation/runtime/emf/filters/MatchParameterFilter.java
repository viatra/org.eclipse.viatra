/**
 * Copyright (c) 2004-2013, Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.transformation.runtime.emf.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pair;

/**
 * A EVM filter that uses a parameter-value map that can be used for
 * multiple patterns and rules.
 * 
 * Use ParameterFilterFactory to create easily manage the mapping and
 * create unmodifiable copies to be added to rules.
 * 
 * @author Abel Hegedus
 */
public class MatchParameterFilter implements EventFilter<IPatternMatch> {
  private Map<String, Object> filterMap;
  
  public MatchParameterFilter(final Map<String, Object> filterMap) {
    this.filterMap = new HashMap<>();
    this.filterMap.putAll(filterMap);
  }
  
  public MatchParameterFilter(final Pair<String, ?>... parameters) {
    this.filterMap = CollectionLiterals.<String, Object>newHashMap(parameters);
  }
  
    @Override
    public boolean isProcessable(final IPatternMatch eventAtom) {
        return eventAtom.parameterNames().stream()
                .anyMatch(it -> (filterMap.containsKey(it) && (!Objects.equals(filterMap.get(it), eventAtom.get(it)))));
    }
  
  public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> Match toMatch(final Matcher matcher) {
      final Match match = matcher.newEmptyMatch();
      matcher.getParameterNames().stream()
          .filter(filterMap::containsKey)
          .forEach(name -> match.set(name, filterMap.get(name)));
      return match;
  }
}
