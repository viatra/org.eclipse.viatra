/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryregistry.properties

import com.google.common.collect.Lists
import java.util.List
import org.eclipse.ui.views.properties.IPropertyDescriptor
import org.eclipse.ui.views.properties.IPropertySource
import org.eclipse.ui.views.properties.PropertyDescriptor
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry

/** 
 * @author Abel Hegedus
 */
class ParametersPropertySource implements IPropertySource {
    final IQuerySpecificationRegistryEntry entry

    new(IQuerySpecificationRegistryEntry adaptableObject) {
        entry = adaptableObject
    }

    override Object getEditableValue() {
        return this
    }
    
    override toString() {
        return ""
    }

    override IPropertyDescriptor[] getPropertyDescriptors() {
        val String category = "Parameters"
        val List<IPropertyDescriptor> parameters = Lists.newArrayList()
        val IQuerySpecification<?> specification = entry.get()
        specification.parameters.forEach[ param |
            val PropertyDescriptor property = new PropertyDescriptor(param.getName(), param.getName())
            property.setCategory(category)
            parameters.add(property)
        ]
        return parameters.toArray(newArrayOfSize(0))
    }

    override Object getPropertyValue(Object id) {
        val IQuerySpecification<?> specification = entry.get()
        val param = specification.parameters.findFirst[name == id]
        return param.typeName
    }

    override boolean isPropertySet(Object id) {
        return false
    }

    override void resetPropertyValue(Object id) {
    }

    override void setPropertyValue(Object id, Object value) {
    }
}
