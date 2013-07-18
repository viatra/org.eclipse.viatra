package org.eclipse.viatra2.emf.runtime.filters

import java.util.Map

// this factory can be updated and used to create multiple filters
class ParemeterFilterFactory {
  
  Map<String, Object> filterMap = newHashMap()
  
  def setFilteredParameter(String parameter, Object value){
    filterMap.put(parameter, value)
  }
  
  def unsetFilteredParameter(String parameter){
    filterMap.remove(parameter)
  }
  
  // we cannot allow changes to the filterMap once a filter was initialized
  def createFilter(){
    new IncQueryMatchParameterEventFilter(filterMap)
  }
}