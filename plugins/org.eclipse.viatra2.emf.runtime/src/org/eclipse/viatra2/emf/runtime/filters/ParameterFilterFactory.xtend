/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.runtime.filters

import java.util.Map

/**
 * This factory can be updated and used to create multiple filters
 * 
 * @author Abel Hegedus
 *
 */class ParemeterFilterFactory {
  
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