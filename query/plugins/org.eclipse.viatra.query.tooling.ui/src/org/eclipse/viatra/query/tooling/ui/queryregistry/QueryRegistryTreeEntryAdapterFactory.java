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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Abel Hegedus
 *
 */
public class QueryRegistryTreeEntryAdapterFactory implements IAdapterFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IPropertySource.class && adaptableObject instanceof QueryRegistryTreeEntry) {
            return adapterType.cast(new QueryRegistryTreeEntryPropertySource((QueryRegistryTreeEntry) adaptableObject));
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class[] { IPropertySource.class };
    }

}
