package org.eclipse.viatra2.emf.runtime.filters

import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.api.IPatternMatch

class IncQueryMatchParameterEventFilter implements EventFilter<IPatternMatch> {
  
  val filterMap = newHashMap()
  
  override isProcessable(IPatternMatch eventAtom) {
    // if the parameter name is included in the filter, but has different value
    eventAtom.parameterNames.filter[
      filterMap.containsKey(it) && filterMap.get(it) != eventAtom.get(it)
    ].empty
    // maybe this is not the best performance-wise :)
  }
  
  def setFilteredParameter(String parameter, Object value){
    filterMap.put(parameter, value)
  }
  
  def unsetFilteredParameter(String parameter){
    filterMap.remove(parameter)
  }
  
}