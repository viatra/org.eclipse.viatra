/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.gmf;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;

/**
 * Adapter factory for the GMF connector.
 */
@SuppressWarnings("rawtypes")
public class ModelConnectorAdapterFactoryForGMFEditors implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IModelConnector.class && adaptableObject instanceof IEditorPart) {
            return new GMFModelConnector((IEditorPart) adaptableObject);
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { IModelConnector.class };
    }

}
