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
package org.eclipse.viatra.query.runtime.ui.modelconnector.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.viatra.query.runtime.ui.modelconnector.EMFModelConnector;
import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector;

/**
 * Adapter factory for the default EMF generated model editors and our own VQL editor.
 */
public class ModelConnectorAdapterFactoryForEMFEditors implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IModelConnector.class && adaptableObject instanceof MultiPageEditorPart) {
            IEditorPart editorPart = (IEditorPart) adaptableObject;
            Object selectedPage = ((MultiPageEditorPart) editorPart).getSelectedPage();
            if (selectedPage instanceof IEditorPart) {
                return Platform.getAdapterManager().loadAdapter(selectedPage, adapterType.getName());
            }
        } if (adapterType == IModelConnector.class && adaptableObject instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart) adaptableObject;
            if (editorPart instanceof IEditingDomainProvider) {
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
