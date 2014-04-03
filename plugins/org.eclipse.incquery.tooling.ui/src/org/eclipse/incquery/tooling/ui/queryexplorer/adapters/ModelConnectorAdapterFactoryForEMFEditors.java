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
package org.eclipse.incquery.tooling.ui.queryexplorer.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.incquery.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

/**
 * Adapter factory for the default EMF generated model editors and our own EIQ editor.
 */
@SuppressWarnings("rawtypes")
public class ModelConnectorAdapterFactoryForEMFEditors implements IAdapterFactory {

    @Inject
    private IResourceSetProvider resourceSetProvider;

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IModelConnector.class && adaptableObject instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart) adaptableObject;
            if ("org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguage".equals(editorPart.getSite().getId())) {
                return new EIQEditorModelConnector(editorPart, resourceSetProvider);
            } else if (adaptableObject instanceof IEditingDomainProvider) {
                return new EMFModelConnector(editorPart);
            }
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { IModelConnector.class };
    }

}
