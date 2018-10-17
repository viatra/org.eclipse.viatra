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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultTreeMatcher;

/**
 * @author Abel Hegedus
 *
 */
public class QueryResultTreeMatcherPropertySource<MATCH extends IPatternMatch> implements IPropertySource {

    private static final String FILTERS_ID = "filters";
    private static final String BACKEND_ID = "backend";
    private static final String HINTS_ID = "hints";
    private QueryResultTreeMatcher<MATCH> matcher;

    public QueryResultTreeMatcherPropertySource(QueryResultTreeMatcher<MATCH> matcher) {
        this.matcher = matcher;
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        String category = "Info";
        PropertyDescriptor hintsProperty = new PropertyDescriptor(HINTS_ID, "Hints");
        hintsProperty.setCategory(category);
        PropertyDescriptor backendProperty = new PropertyDescriptor(BACKEND_ID, "Backend");
        backendProperty.setCategory(category);
        PropertyDescriptor matchCountProperty = new PropertyDescriptor(FILTERS_ID, "Filters");
        matchCountProperty.setCategory(category);
        return new IPropertyDescriptor[] { 
                backendProperty,
                hintsProperty,
                matchCountProperty};
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals(HINTS_ID)) {
            if(matcher.getHint() != null) {
                return new HintsPropertySource(matcher.getHint());
            }
            return "Unknown";
        }
        if (id.equals(BACKEND_ID)) {
            if(matcher.getHint() != null){
                return matcher.getHint().getQueryBackendFactory().getBackendClass().getSimpleName();
            }
            return "Unknown";
        }
        if (id.equals(FILTERS_ID)) {
            return new MatcherFiltersPropertySource<MATCH>(matcher);
        }
        return null;
    }

    @Override
    public boolean isPropertySet(Object id) {
        if (id.equals(FILTERS_ID)) {
            return matcher.isFiltered();
        }
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
        if (id.equals(FILTERS_ID)) {
            matcher.resetFilter();
        }
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
    }

}
