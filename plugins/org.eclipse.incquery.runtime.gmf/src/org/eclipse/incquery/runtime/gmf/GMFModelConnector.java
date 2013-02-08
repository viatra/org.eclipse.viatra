/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.gmf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.tooling.ui.queryexplorer.adapters.EMFModelConnector;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorPart;

/**
 * FIXME DO IT
 */
public class GMFModelConnector extends EMFModelConnector {

    public GMFModelConnector(IEditorPart editorPart) {
        super(editorPart);
    }

    @Override
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier result = null;
        if (IModelConnectorTypeEnum.RESOURCESET.equals(modelConnectorTypeEnum)) {
            if (editorPart instanceof DiagramDocumentEditor) {
                DiagramDocumentEditor diagramDocumentEditor = (DiagramDocumentEditor) editorPart;
                return diagramDocumentEditor.getEditingDomain().getResourceSet();
            } else if (editorPart instanceof IDiagramWorkbenchPart) {
                IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) editorPart;
                return diagramWorkbenchPart.getDiagramEditPart().getEditingDomain().getResourceSet();
            }
        } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum)) {
            if (editorPart instanceof DiagramDocumentEditor) {
                DiagramDocumentEditor diagramDocumentEditor = (DiagramDocumentEditor) editorPart;
                return ((EObject) diagramDocumentEditor.getDiagramEditPart().getModel()).eResource();
            } else if (editorPart instanceof IDiagramWorkbenchPart) {
                IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) editorPart;
                return ((EObject) diagramWorkbenchPart.getDiagramEditPart().getModel()).eResource();
            }
        } else if (IModelConnectorTypeEnum.EOBJECT.equals(modelConnectorTypeEnum)) {
            // XXX Not implemented now. The selected element is a would be a graphical, not a model object.
        }
        return result;
    }

    @Override
    protected TreePath createTreePath(IEditorPart editor, EObject obj) {
        if (editor instanceof DiagramDocumentEditor) { // TODO: also handle IDiagramWorkbenchPart
            DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editor;
            EditPart epBegin = providerEditor.getDiagramEditPart().getPrimaryChildEditPart();
            if (epBegin instanceof GraphicalEditPart) {
                List<Object> nodes = new ArrayList<Object>();
                epBegin = ((GraphicalEditPart) epBegin).findEditPart(epBegin.getRoot(), obj);
                if (epBegin != null) {
                    nodes.add(epBegin);
                    return new TreePath(nodes.toArray());
                }
            }
        }
        return null;
    }

    @Override
    protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
        super.navigateToElements(editorPart, selection);
        if (editorPart instanceof DiagramDocumentEditor) {
            DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editorPart;
            IDiagramGraphicalViewer viewer = providerEditor.getDiagramGraphicalViewer();
            if (selection.getFirstElement() instanceof GraphicalEditPart) {
                GraphicalEditPart part = (GraphicalEditPart) selection.getFirstElement();
                viewer.reveal(part);
            }
        }
        // FIXME do it: also support IDiagramWorkbenchPart
    }
    
}
