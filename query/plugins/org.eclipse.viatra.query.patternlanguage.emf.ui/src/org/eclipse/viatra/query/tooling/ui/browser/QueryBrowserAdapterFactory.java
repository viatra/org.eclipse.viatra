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
package org.eclipse.viatra.query.tooling.ui.browser;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.properties.IQuerySpecificationRegistryEntryPropertySource;
import org.eclipse.viatra.query.tooling.ui.queryregistry.properties.QueryRegistryTreeEntryPropertySource;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultTreeMatcher;
import org.eclipse.viatra.query.tooling.ui.queryresult.properties.QueryResultTreeMatchPropertySource;
import org.eclipse.viatra.query.tooling.ui.queryresult.properties.QueryResultTreeMatcherPropertySource;
import org.eclipse.viatra.query.tooling.ui.queryresult.properties.ValueWrapperAwarePropertySource;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBrowserAdapterFactory implements IAdapterFactory {

    private AdapterFactoryContentProvider adapterFactoryContentProvider;

    public QueryBrowserAdapterFactory() {
        ReflectiveItemProviderAdapterFactory adapterFactory = new ReflectiveItemProviderAdapterFactory();
        adapterFactoryContentProvider = new AdapterFactoryContentProvider(adapterFactory);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IPropertySource.class) {
            if (adaptableObject instanceof QueryRegistryTreeEntry) {
                return adapterType.cast(new QueryRegistryTreeEntryPropertySource((QueryRegistryTreeEntry) adaptableObject));
            } else if (adaptableObject instanceof IQuerySpecificationRegistryEntry) {
                return adapterType.cast(new IQuerySpecificationRegistryEntryPropertySource((IQuerySpecificationRegistryEntry) adaptableObject));
            } else if (adaptableObject instanceof QueryResultTreeMatcher) {
                return adapterType.cast(new QueryResultTreeMatcherPropertySource((QueryResultTreeMatcher) adaptableObject));
            } else if(adaptableObject instanceof IPatternMatch) {
                return adapterType.cast(new QueryResultTreeMatchPropertySource((IPatternMatch) adaptableObject));
            } else if(adaptableObject instanceof EObject) {
                return adapterType.cast(new ValueWrapperAwarePropertySource(adapterFactoryContentProvider.getPropertySource(adaptableObject)));
            }
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class[] { IPropertySource.class };
    }
    
}
