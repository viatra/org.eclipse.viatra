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
package org.eclipse.viatra.query.tooling.ui.queryregistry;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Abel Hegedus
 *
 */
public class QueryRegistryTreeEntryPropertySource implements IPropertySource {

    private final QueryRegistryTreeEntry entry;
    
    /**
     * @param adaptableObject
     */
    public QueryRegistryTreeEntryPropertySource(QueryRegistryTreeEntry adaptableObject) {
        entry = adaptableObject;
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new PropertyDescriptor("fqn", "Fully qualified name"),
                new PropertyDescriptor("source", "Source identifier"),
                new PropertyDescriptor("loaded", "Query specification loaded")};
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals("fqn")) {
            return entry.getEntry().getFullyQualifiedName();
          }
          if (id.equals("source")) {
            return entry.getEntry().getSourceIdentifier();
          }
          if (id.equals("loaded")) {
              return entry.isLoaded();
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
