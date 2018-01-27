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

import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * A EVM filter that uses a parameter-value map that can be used for
 * multiple patterns and rules.
 * 
 * Use ParameterFilterFactory to create easily manage the mapping and
 * create unmodifiable copies to be added to rules.
 * 
 * @author Abel Hegedus
 */
@SuppressWarnings("all")
public class MatchParameterFilter implements EventFilter<IPatternMatch> {
  private Map<String, Object> filterMap;
  
  public MatchParameterFilter(final Map<String, Object> filterMap) {
    HashMap<String, Object> _newHashMap = CollectionLiterals.<String, Object>newHashMap();
    this.filterMap = _newHashMap;
    this.filterMap.putAll(filterMap);
  }
  
  public MatchParameterFilter(final Pair<String, ?>... parameters) {
    HashMap<String, Object> _newHashMap = CollectionLiterals.<String, Object>newHashMap(parameters);
    this.filterMap = _newHashMap;
  }
  
  @Override
  public boolean isProcessable(final IPatternMatch eventAtom) {
    List<String> _parameterNames = eventAtom.parameterNames();
    final Function1<String, Boolean> _function = new Function1<String, Boolean>() {
      @Override
      public Boolean apply(final String it) {
        boolean _and = false;
        boolean _containsKey = MatchParameterFilter.this.filterMap.containsKey(it);
        if (!_containsKey) {
          _and = false;
        } else {
          Object _get = MatchParameterFilter.this.filterMap.get(it);
          Object _get_1 = eventAtom.get(it);
          boolean _notEquals = (!Objects.equal(_get, _get_1));
          _and = _notEquals;
        }
        return Boolean.valueOf(_and);
      }
    };
    Iterable<String> _filter = IterableExtensions.<String>filter(_parameterNames, _function);
    return IterableExtensions.isEmpty(_filter);
  }
  
  public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> Match toMatch(final Matcher matcher) {
    Match _xblockexpression = null;
    {
      final Match match = matcher.newEmptyMatch();
      List<String> _parameterNames = matcher.getParameterNames();
      final Procedure1<String> _function = new Procedure1<String>() {
        @Override
        public void apply(final String it) {
          boolean _containsKey = MatchParameterFilter.this.filterMap.containsKey(it);
          if (_containsKey) {
            Object _get = MatchParameterFilter.this.filterMap.get(it);
            match.set(it, _get);
          }
        }
      };
      IterableExtensions.<String>forEach(_parameterNames, _function);
      _xblockexpression = match;
    }
    return _xblockexpression;
  }
}
