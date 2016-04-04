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
package org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

/**
 * Adapter factory for the default EMF generated model editors and our own VQL editor.
 */
public class ModelConnectorAdapterFactoryForEMFEditors implements IAdapterFactory {

    @Inject IResourceSetProvider resourceSetProvider;

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IModelConnector.class && adaptableObject instanceof MultiPageEditorPart) {
            IEditorPart editorPart = (IEditorPart) adaptableObject;
            Object selectedPage = ((MultiPageEditorPart) editorPart).getSelectedPage();
            if (selectedPage instanceof IEditorPart) {
                Platform.getAdapterManager().loadAdapter((IEditorPart)selectedPage, adapterType.getName());
                return Platform.getAdapterManager().getAdapter((IEditorPart)selectedPage, adapterType);
            }
        } if (adapterType == IModelConnector.class && adaptableObject instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart) adaptableObject;
            if ("org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguage".equals(editorPart.getSite().getId())) {
                return new VQLEditorModelConnector(editorPart, resourceSetProvider);
            } else if (editorPart instanceof IEditingDomainProvider) {
                return new EMFModelConnector(editorPart);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class[] { IModelConnector.class };
    }

}
