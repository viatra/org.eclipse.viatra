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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.ui.modelconnector.EMFModelConnector;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

/**
 * Model connector implementation for our own VQL model editor.
 */
public class VQLEditorModelConnector extends EMFModelConnector {

    private final IResourceSetProvider resourceSetProvider;

    public VQLEditorModelConnector(IEditorPart editorPart, IResourceSetProvider resourceSetProvider) {
        super(editorPart);
        this.resourceSetProvider = resourceSetProvider;
    }

    @Override
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier result = null;
        if (IModelConnectorTypeEnum.RESOURCESET.equals(modelConnectorTypeEnum)) {
            // XXX It should load the depending vql's as well
            IFile file = editorPart.getEditorInput().getAdapter(IFile.class);
            if (file != null) {
                result = loadVQLFile(file);
            }
        } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum)) {
            IFile file = editorPart.getEditorInput().getAdapter(IFile.class);
            if (file != null) {
                result = loadVQLFile(file);
            }
        }
        return result;
    }

    private Resource loadVQLFile(IFile file) {
        ResourceSet resourceSet = resourceSetProvider.get(file.getProject());
        URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        return resourceSet.getResource(fileURI, true);
    }

    @Override
    public void showLocation(Object[] locationObjects) {
        // TODO
    }

}
