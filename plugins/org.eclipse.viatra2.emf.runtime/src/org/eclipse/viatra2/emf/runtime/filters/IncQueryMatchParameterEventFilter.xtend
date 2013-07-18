package org.eclipse.viatra2.emf.runtime.filters

import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.api.IPatternMatch
import java.util.Map

// this filter can be used for EVM rules
class IncQueryMatchParameterEventFilter implements EventFilter<IPatternMatch> {
  
  Map<String,Object> filterMap
  
  new(Map<String,Object> filterMap) {
    this.filterMap = newHashMap()
    this.filterMap.putAll(filterMap)
  }
  
  override isProcessable(IPatternMatch eventAtom) {
    // if the parameter name is included in the filter, but has different value
    eventAtom.parameterNames.filter[
      filterMap.containsKey(it) && filterMap.get(it) != eventAtom.get(it)
    ].empty
    // maybe this is not the best performance-wise :)
  }
  
}

