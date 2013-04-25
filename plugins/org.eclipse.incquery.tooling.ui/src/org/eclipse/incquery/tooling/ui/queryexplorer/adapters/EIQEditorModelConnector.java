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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

/**
 * Model connector implementation for our own EIQ model editor.
 */
public class EIQEditorModelConnector extends EMFModelConnector {

    private final IResourceSetProvider resourceSetProvider;

    public EIQEditorModelConnector(IEditorPart editorPart, IResourceSetProvider resourceSetProvider) {
        super(editorPart);
        this.resourceSetProvider = resourceSetProvider;
    }

    @Override
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier result = null;
        if (IModelConnectorTypeEnum.RESOURCESET.equals(modelConnectorTypeEnum)) {
            // XXX It should load the depending eiq's as well
            IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
            if (file != null) {
                result = loadEIQFile(file);
            }
        } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum)) {
            IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
            if (file != null) {
                result = loadEIQFile(file);
            }
        }
        return result;
    }

    private Resource loadEIQFile(IFile file) {
        ResourceSet resourceSet = resourceSetProvider.get(file.getProject());
        URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        return resourceSet.getResource(fileURI, true);
    }

    @Override
    public void showLocation(Object[] locationObjects) {
        // TODO
    }

}
