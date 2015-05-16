/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.facet.browser;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.incquery.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.ui.IEditorPart;

/**
 * Adapter factory for the Facet model connector.
 */
@SuppressWarnings("rawtypes")
public class ModelConnectorAdapterFactoryForFacet implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IModelConnector.class) {
            if (adaptableObject instanceof IEditorPart) {
                return new FacetModelConnector((IEditorPart) adaptableObject);
            }
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { IModelConnector.class };
    }

}
