/*******************************************************************************
 * Copyright (c) 2010-2018, Balint Lorand, Abel Hegedus, Istvan Rath and Daniel Varro, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balint Lorand - initial API and implementation
 *   Abel Hegedus - minor changes
 *   Zoltan Ujhelyi - adaptation for use with Sirius
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.business.api.query.DRepresentationQuery;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.business.api.query.EObjectQuery;
import org.eclipse.sirius.diagram.ui.business.api.view.SiriusGMFHelper;
import org.eclipse.sirius.diagram.ui.tools.internal.marker.SiriusMarkerNavigationProviderSpec;
import org.eclipse.sirius.viewpoint.DRepresentationDescriptor;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.viatra.addon.validation.core.api.IEntry;
import org.eclipse.viatra.addon.validation.core.api.IViolation;
import org.eclipse.viatra.addon.validation.core.listeners.ConstraintListener;
import org.eclipse.viatra.addon.validation.core.listeners.ViolationListener;

public class VGQLViolationListener implements ConstraintListener, ViolationListener {

    private final Logger logger;
    private final Map<IViolation, IMarker> markerMap = new HashMap<>();
    private final IFile editorLocation;
    private final DiagramEditPart diagramEditPart;

    /**
     * @since 2.1
     */
    public VGQLViolationListener(DiagramEditPart diagramEditPart, IFile editorLocation, Logger logger) {
        super();
        this.diagramEditPart = diagramEditPart;
        this.editorLocation = Objects.requireNonNull(editorLocation);
        this.logger = logger;
    }

    @Override
    public void violationAppeared(IViolation violation) {
        List<String> keyNames = violation.getConstraint().getSpecification().getKeyNames();
        Map<String, Object> keyObjects = violation.getKeyObjects();
        for (String keyName : keyNames) {
            Object keyObject = keyObjects.get(keyName);
            if (keyObject instanceof EObject) {
                EObject location = (EObject) keyObject;
                if (location.eResource() != null) {
                    View view = getCorrespondingView(location, diagramEditPart);
                    int statusSeverity;
                    switch(violation.getConstraint().getSpecification().getSeverity()) {
                    case INFO:
                            statusSeverity = IStatus.INFO;
                            break;
                    case WARNING:
                            statusSeverity = IStatus.WARNING;
                            break;
                    case ERROR:
                    default:
                        statusSeverity = IStatus.ERROR;
                    }
                    IMarker marker = addMarker(diagramEditPart.getViewer(), editorLocation, view,
                            EMFCoreUtil.getQualifiedName(location, true), violation.getMessage(),
                            statusSeverity);
                
                    markerMap.put(violation, marker);
                    violation.addListener(this);
                }
            }
            break;
        }
    }

    @Override
    public void violationDisappeared(IViolation violation) {
        IMarker marker = markerMap.remove(violation);
        if (marker != null) {
            try {
                marker.delete();
            } catch (CoreException e) {
                logger.error("Could not delete marker!", e);
            }
        }

    }

    @Override
    public void violationEntryAppeared(IViolation violation, IEntry entry) {
        // entries not handled in markers currently
    }

    @Override
    public void violationMessageUpdated(IViolation violation) {
        IMarker marker = markerMap.get(violation);
        if (marker != null) {
            try {
                marker.setAttribute(IMarker.MESSAGE, violation.getMessage());
            } catch (CoreException e) {
                logger.error("Error during marker update!", e);
            }
        }
    }

    @Override
    public void violationEntryDisappeared(IViolation violation, IEntry entry) {
        // entries not handled in markers currently
    }

    @Override
    public void dispose() {
        for (IMarker marker : markerMap.values()) {
            try {
                marker.delete();
            } catch (CoreException e) {
                logger.error(String.format("Exception occured when removing a marker on dispose: %s", e.getMessage()),
                        e);
            }
        }
    }

    /**
     * Copied from org.eclipse.sirius.diagram.ui.part.ValidateAction.getCorrespondingView(EObject, DiagramEditPart)
     */
    private static View getCorrespondingView(EObject element, DiagramEditPart diagramEditPart) {
        DSemanticDecorator dSemanticDecorator = getDSemanticDecorator(element, diagramEditPart);
        View view = null;
        if (dSemanticDecorator != null) {
            view = SiriusGMFHelper.getGmfView(dSemanticDecorator);
        }
        if (view == null) {
            view = diagramEditPart.getDiagramView();
        }
        return view;
    }
    
    /**
     * Copied from org.eclipse.sirius.diagram.ui.part.ValidateAction.getDSemanticDecorator(EObject, DiagramEditPart)
     */
    private static DSemanticDecorator getDSemanticDecorator(EObject element, DiagramEditPart diagramEditPart) {
        DSemanticDecorator dSemanticDecorator = null;
        if (!(element instanceof DSemanticDecorator)) {
            Collection<EObject> xrefs = new EObjectQuery(element).getInverseReferences(ViewpointPackage.Literals.DSEMANTIC_DECORATOR__TARGET);
            DDiagram dDiagram = (DDiagram) diagramEditPart.getDiagramView().getElement();
            for (EObject eObject : xrefs) {
                if (eObject == dDiagram || eObject instanceof DSemanticDecorator && EcoreUtil.isAncestor(dDiagram, eObject)) {
                    dSemanticDecorator = (DSemanticDecorator) eObject;
                    break;
                }
            }
        } else {
            dSemanticDecorator = (DSemanticDecorator) element;
        }
        return dSemanticDecorator;
    }
    
    /**
     * Copied from org.eclipse.sirius.diagram.ui.part.ValidateAction.addMarker(EditPartViewer, IFile, View, String, String, int)
     */
    private static IMarker addMarker(EditPartViewer viewer, IFile target, View view, String location, String message, int statusSeverity) {
        String elementId = view.eResource().getURIFragment(view);
        // Search semantic URI
        String semanticURI = null;
        EObject ddiagramElement = view.getElement();
        if (ddiagramElement instanceof DSemanticDecorator) {
            EObject semanticElement = ((DSemanticDecorator) ddiagramElement).getTarget();
            semanticURI = EcoreUtil.getURI(semanticElement).toString();
        }
        // Search diagram URI
        Object object = viewer.getFocusEditPart().getModel();
        String diagramDescriptorUri = getDRepresentationDescriptorURIFromDiagram(object);
        return SiriusMarkerNavigationProviderSpec.addMarker(target, elementId, diagramDescriptorUri, semanticURI, location, message, statusSeverity);
    }
    
    /**
     * Copied from org.eclipse.sirius.diagram.ui.part.ValidateAction.getDRepresentationDescriptorURIFromDiagram(Object)
     */
    private static String getDRepresentationDescriptorURIFromDiagram(Object object) {
        String diagramDescriptorUri = null;
        if (object instanceof Diagram) {
            Optional<DRepresentationDescriptor> optional = Optional.of((Diagram) object).map(View::getElement).filter(DDiagram.class::isInstance).map(d -> {
                DRepresentationQuery query = new DRepresentationQuery((DDiagram) d);
                return query.getRepresentationDescriptor();
            });
            if (optional.isPresent()) {
                final URI uri = EcoreUtil.getURI(optional.get());
                diagramDescriptorUri = uri.toString();
            }
        }
        return diagramDescriptorUri;
    }
}
