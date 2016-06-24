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

import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultTreeMatcher;

/**
 * @author Abel Hegedus
 *
 */
public class QueryResultTreeMatcherPropertySource implements IPropertySource {

    private static final String FILTERS_ID = "filters";
    private QueryResultTreeMatcher matcher;

    public QueryResultTreeMatcherPropertySource(QueryResultTreeMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        String category = "Info";
        PropertyDescriptor matchCountProperty = new PropertyDescriptor(FILTERS_ID, "Filters");
        matchCountProperty.setCategory(category);
        return new IPropertyDescriptor[] { 
                matchCountProperty};
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals(FILTERS_ID)) {
            // TODO return FiltersPropertySource 
            return "Filtering not yet supported";
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
