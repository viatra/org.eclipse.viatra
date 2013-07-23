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

import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.api.IPatternMatch
import java.util.Map

/**
 * A EVM filter that uses a parameter-value map that can be used for 
 * multiple patterns and rules.
 * 
 * Use ParameterFilterFactory to create easily manage the mapping and
 * create unmodifiable copies to be added to rules.
 * 
 * @author Abel Hegedus
 *
 */
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

