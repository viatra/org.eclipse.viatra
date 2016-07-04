/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult.properties;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

import com.google.common.collect.Lists;

/**
 * @author Abel Hegedus
 *
 */
public class QueryResultTreeMatchPropertySource implements IPropertySource {

    private IPatternMatch match;
    private AdapterFactoryLabelProvider adapterFactoryLabelProvider;

    public QueryResultTreeMatchPropertySource(IPatternMatch match) {
        this.match = match;
        ReflectiveItemProviderAdapterFactory adapterFactory = new ReflectiveItemProviderAdapterFactory();
        adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        String category = "Parameters";
        List<IPropertyDescriptor> parameters = Lists.newArrayList();
        for (String paramName : match.parameterNames()) {
            PropertyDescriptor property = new PropertyDescriptor(paramName, paramName);
            property.setCategory(category);
            parameters.add(property);
        }
        return parameters.toArray(new IPropertyDescriptor[0]);
    }

    @Override
    public Object getPropertyValue(Object id) {
        
        // return EMF edit property source for each parameter
        Object paramValue = match.get((String) id);
        if(paramValue instanceof EObject) {
            return adapterFactoryLabelProvider.getText(paramValue);
        }
        return paramValue;
    }

    @Override
    public boolean isPropertySet(Object id) {
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
    }

}
