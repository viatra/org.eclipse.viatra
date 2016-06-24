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
package org.eclipse.viatra.query.tooling.ui.queryregistry.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;

/**
 * @author Abel Hegedus
 *
 */
public class QueryRegistryTreeEntryPropertySource implements IPropertySource {

    private static final String PROPERTY_FQN = "fqn";
    private static final String PROPERTY_LOADED = "loaded";
    private static final String PROPERTY_PROJECT = "project";
    private static final String PROPERTY_SOURCE = "source";
    private static final String PROPERTY_PARAMETERS = "parameters";
    
    private final QueryRegistryTreeEntry entry;
    
    public QueryRegistryTreeEntryPropertySource(QueryRegistryTreeEntry adaptableObject) {
        entry = adaptableObject;
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        String category = "Info";
        PropertyDescriptor fqnProperty = new PropertyDescriptor(PROPERTY_FQN, "Fully qualified name");
        fqnProperty.setCategory(category);
        PropertyDescriptor sourceProperty = new PropertyDescriptor(PROPERTY_SOURCE, "Source identifier");
        sourceProperty.setCategory(category);
        PropertyDescriptor projectProperty = new PropertyDescriptor(PROPERTY_PROJECT, "Contributing project");
        projectProperty.setCategory(category);
        PropertyDescriptor loadedProperty = new PropertyDescriptor(PROPERTY_LOADED, "Query specification loaded");
        loadedProperty.setCategory(category);
        PropertyDescriptor parametersProperty = new PropertyDescriptor(PROPERTY_PARAMETERS, "Parameters");
        parametersProperty.setCategory(category);
        return new IPropertyDescriptor[] { 
                fqnProperty,
                sourceProperty,
                projectProperty,
                loadedProperty,
                parametersProperty};
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals(PROPERTY_FQN)) {
            return entry.getEntry().getFullyQualifiedName();
        }
        if (id.equals(PROPERTY_SOURCE)) {
            return entry.getEntry().getSourceIdentifier();
        }
        if (id.equals(PROPERTY_LOADED)) {
            return entry.isLoaded();
        }
        if (id.equals(PROPERTY_PROJECT)) {
            return entry.getEntry().getSourceProjectName();
        }
        if (id.equals(PROPERTY_PARAMETERS)) {
            if(entry.isLoaded()){
                return new ParametersPropertySource(entry.getEntry());
            } else {
                return "Query specification not loaded";
            }
        }
        return null;
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
