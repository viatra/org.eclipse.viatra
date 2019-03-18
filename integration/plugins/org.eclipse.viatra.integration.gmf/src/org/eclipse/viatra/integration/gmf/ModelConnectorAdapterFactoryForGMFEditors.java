/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.gmf;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector;

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
