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
package org.eclipse.viatra.integration.graphiti;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.EMFModelConnector;

/**
 * Model connector implementation for the Graphiti model editors.
 */
public class GraphitiModelConnector extends EMFModelConnector {

    public GraphitiModelConnector(IEditorPart editorPart) {
        super(editorPart);
    }

    @Override
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier result = null;
        if (editorPart instanceof DiagramEditor) {
            if (IModelConnectorTypeEnum.RESOURCESET.equals(modelConnectorTypeEnum)) {
                DiagramEditor diagramEditor = (DiagramEditor) editorPart;
                return diagramEditor.getEditingDomain().getResourceSet();
            } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum)) {
                DiagramEditor diagramEditor = (DiagramEditor) editorPart;
                PictogramElement[] selectedElements = diagramEditor.getSelectedPictogramElements();
                if (selectedElements.length > 0) {
                    PictogramElement element = selectedElements[0];
                    EObject businessObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(
                            element);
                    if (businessObject != null) {
                        return businessObject.eResource();
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void showLocation(Object[] locationObjects) {
        // reflective set selection is not needed
        IStructuredSelection preparedSelection = prepareSelection(locationObjects);
        navigateToElements((IEditorPart) getOwner(), preparedSelection);
        workbenchPage.bringToTop(getOwner());
        // reflectiveSetSelection(key.getEditorPart(), preparedSelection);
    }

    @Override
    protected TreePath createTreePath(IEditorPart editor, EObject obj) {
        if (editor instanceof DiagramEditor) {
            Diagram diagram = ((DiagramEditor) editor).getDiagramTypeProvider().getDiagram();
            List<PictogramElement> pictogramElements = Graphiti.getLinkService().getPictogramElements(diagram, obj);
            if (!pictogramElements.isEmpty()) {
                List<EditPart> parts = new ArrayList<EditPart>();
                for (PictogramElement element : pictogramElements) {
                    EditPart part = findEditPart((DiagramEditor) editor, element);
                    if (part != null) {
                        parts.add(part);
                    }
                }
                return new TreePath(parts.toArray());
            }
        }
        return null;
    }
    
    private EditPart findEditPart(DiagramEditor editor, PictogramElement element) {
         try {
             //pre-0.10: return editor.getEditPartForPictogramElement(element);
             //since 0.10: return editor.getDiagramBehavior().getEditPartForPictogramElement(element);
             Method m = null;
             try {
                 m = editor.getClass().getMethod("getEditPartForPictogramElement");                 
             } catch (NoSuchMethodException e) {
                 m = editor.getClass().getMethod("getDiagramBehavior");
                 Class<?> behaviourClass = m.invoke(element).getClass();
                 m = behaviourClass.getMethod("getEditPartForPictogramElement");                 
             }
             if (m != null) {
                 return (EditPart) m.invoke(editorPart, element);
             }
         } catch (Exception e) {
            logger.log(new Status(IStatus.ERROR,
                    "org.eclipse.viatra.integration.graphiti",
                    "Error while connecting to Graphiti based editor", e));
         }
         return null;
    }

    @Override
    protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
        if (editorPart instanceof DiagramEditor) {
            DiagramEditor providerEditor = (DiagramEditor) editorPart;
            providerEditor.getGraphicalViewer().setSelection(selection);
        }
    }

    @Override
    protected Collection<EObject> getSelectedEObjects(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            return Arrays.stream(((IStructuredSelection) selection).toArray())
            .filter(GraphitiShapeEditPart.class::isInstance)
            .map(GraphitiShapeEditPart.class::cast)
            .map(input -> Graphiti.getLinkService()
                    .getBusinessObjectForLinkedPictogramElement(input.getPictogramElement()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        } else {
            return super.getSelectedEObjects();
        }
    }
}
