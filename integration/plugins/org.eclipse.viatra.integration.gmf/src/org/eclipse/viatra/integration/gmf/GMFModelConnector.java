/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *   Istvan Rath - bugfixes
 *******************************************************************************/
package org.eclipse.viatra.integration.gmf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.EMFModelConnector;

/**
 * Model connector implementation for the GMF editors.
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
                Diagram diagram = diagramDocumentEditor.getDiagram();
                if (diagram != null && diagram.getElement() != null) {
                    return diagram.getElement().eResource();
                } else {
                    return ((EObject) diagramDocumentEditor.getDiagramEditPart().getModel()).eResource();
                }
            } else if (editorPart instanceof IDiagramWorkbenchPart) {
                IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) editorPart;
                Diagram diagram = diagramWorkbenchPart.getDiagram();
                if (diagram != null && diagram.getElement() != null) {
                    return diagram.getElement().eResource();
                } else {
                    return ((EObject) diagramWorkbenchPart.getDiagramEditPart().getModel()).eResource();
                }
            }
        }
        return result;
    }

    @Override
    protected TreePath createTreePath(IEditorPart editor, EObject obj) {
        if (editor instanceof DiagramDocumentEditor) {
            DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editor;
            return createTreePath(providerEditor.getDiagramEditPart().getPrimaryChildEditPart(), obj);
        } else if (editor instanceof IDiagramWorkbenchPart) {
            IDiagramWorkbenchPart dwp = (IDiagramWorkbenchPart) editor;
            return createTreePath(dwp.getDiagramEditPart().getPrimaryChildEditPart(), obj);
        }
        return null;
    }

    private TreePath createTreePath(EditPart epBegin, EObject obj) {
        if (epBegin instanceof GraphicalEditPart) {
            List<Object> nodes = new ArrayList<Object>();
            epBegin = ((GraphicalEditPart) epBegin).findEditPart(epBegin.getRoot(), obj);
            if (epBegin != null) {
                nodes.add(epBegin);
                return new TreePath(nodes.toArray());
            }
        }
        return null;
    }

    @Override
    protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
        super.navigateToElements(editorPart, selection);
        IDiagramGraphicalViewer viewer = null;
        if (editorPart instanceof DiagramDocumentEditor) {
            DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editorPart;
            viewer = providerEditor.getDiagramGraphicalViewer();
        } else if (editorPart instanceof IDiagramWorkbenchPart) {
            IDiagramWorkbenchPart dwp = (IDiagramWorkbenchPart) editorPart;
            viewer = dwp.getDiagramGraphicalViewer();
        }
        if (viewer != null) {
            if (selection.getFirstElement() instanceof GraphicalEditPart) {
                GraphicalEditPart part = (GraphicalEditPart) selection.getFirstElement();
                viewer.reveal(part);
            }
        }
    }

}
