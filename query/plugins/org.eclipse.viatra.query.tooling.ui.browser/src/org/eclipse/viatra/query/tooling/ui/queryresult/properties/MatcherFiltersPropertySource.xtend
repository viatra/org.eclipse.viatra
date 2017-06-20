/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryresult.properties

import com.google.common.collect.Lists
import java.util.List
import java.util.Map
import org.eclipse.ui.views.properties.IPropertyDescriptor
import org.eclipse.ui.views.properties.IPropertySource
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultTreeMatcher

/** 
 * @author Abel Hegedus
 */
class MatcherFiltersPropertySource implements IPropertySource {
    val QueryResultTreeMatcher matcher
    val Map<PParameter, IPropertyDescriptor> descriptors = newHashMap()
        
    new(QueryResultTreeMatcher matcher) {
        this.matcher = matcher
    }

    override Object getEditableValue() {
        return this
    }
    
    override toString() {
        if(matcher.filtered){
            return "Filtered"
        }
        return "No filters specified"
    }

    override IPropertyDescriptor[] getPropertyDescriptors() {
        val List<IPropertyDescriptor> filters = Lists.newArrayList()
        val parameters = matcher.matcher.specification.parameters as List<PParameter>
        if(descriptors.empty) {
            parameters.forEach[
                val property = new MatchParameterPropertyDescriptor(it, matcher)
                descriptors.put(it, property)
                filters.add(property)
            ]
        } else {
            // return in correct order
            parameters.forEach[
                filters.add(descriptors.get(it))
            ]
        }
        return filters.toArray(newArrayOfSize(0))
    }

    override Object getPropertyValue(Object id) {
        val PParameter param = id as PParameter
        return matcher.filterMatch.get(param.name)
    }

    override boolean isPropertySet(Object id) {
        val PParameter param = id as PParameter
        return matcher.filterMatch.get(param.name) !== null
    }

    override void resetPropertyValue(Object id) {
        val PParameter param = id as PParameter
        matcher.filterMatch.set(param.name, null)
        matcher.filterUpdated(matcher.filterMatch)
    }

    override void setPropertyValue(Object id, Object value) {
        val PParameter param = id as PParameter
        matcher.filterMatch.set(param.name, value)
        matcher.filterUpdated(matcher.filterMatch)
    }
}
