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
package org.eclipse.incquery.runtime.gmf.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * This AdapterFactory is responsible for processing the GMF model inputs, and return the underlying Resource
 * instances from it (corresponding to the logical model).
 */
@SuppressWarnings("rawtypes")
public class ResourceAdapterFactoryForGMFEditors implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == Resource.class) {
            if (adaptableObject instanceof DiagramDocumentEditor) {
                DiagramDocumentEditor diagramDocumentEditor = (DiagramDocumentEditor) adaptableObject;
                //return diagramDocumentEditor.getEditingDomain().getResourceSet();
                return ((EObject)diagramDocumentEditor.getDiagramEditPart().getModel()).eResource();
            } else if (adaptableObject instanceof IDiagramWorkbenchPart) {
                IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) adaptableObject;
                //return diagramWorkbenchPart.getDiagramEditPart().getEditingDomain().getResourceSet();
                return ((EObject)diagramWorkbenchPart.getDiagramEditPart().getModel()).eResource();
            }
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { Resource.class };
    }

}
