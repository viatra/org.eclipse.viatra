/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.validation.core.ValidationEngine;
import org.eclipse.incquery.validation.core.api.IConstraint;
import org.eclipse.incquery.validation.core.api.IConstraintSpecification;
import org.eclipse.incquery.validation.core.api.IValidationEngine;
import org.eclipse.incquery.validation.core.api.IViolation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * The constraint adapter class is used to collect the constraints and deal with their maintenance for a given EMF
 * instance model. If the validation framework is initialized an instance of this class will be created which handles
 * the creation of the appropriate rules and their jobs.
 * 
 * @author Tamas Szabo
 */
public class ConstraintAdapter {

    private Map<IPatternMatch, IMarker> markerMap;
    private Map<IViolation, IMarker> violationMarkerMap;
    private IResource resourceForEditor;
    private IValidationEngine engine;
    private Logger logger;

    public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
        this.logger = logger;
        resourceForEditor = getIResourceForEditor(editorPart);
        this.markerMap = new HashMap<IPatternMatch, IMarker>();
        this.violationMarkerMap = new HashMap<IViolation, IMarker>();

        try {
            IncQueryEngine iqengine = IncQueryEngine.on(new EMFScope(notifier));
            engine = ValidationEngine.builder().setEngine(iqengine).setLogger(logger).build();
            engine.initialize();
            
            MarkerManagerViolationListener markerManagerViolationListener = new MarkerManagerViolationListener(logger, this);
            Set<IConstraintSpecification> constraintSpecificationsForEditorId = ValidationManager
                    .getConstraintSpecificationsForEditorId(editorPart.getSite().getId());
            for (IConstraintSpecification constraint : constraintSpecificationsForEditorId) {
                IConstraint coreConstraint = engine.addConstraintSpecification(constraint);
                coreConstraint.addListener(markerManagerViolationListener);
            }
        } catch (IncQueryException e) {
            logger.error(String.format("Exception occured during validation initialization: %s", e.getMessage()), e);
        }

    }

    private IResource getIResourceForEditor(IEditorPart editorPart) {
        // get resource for editor input (see org.eclipse.ui.ide.ResourceUtil.getResource)
        IEditorInput input = editorPart.getEditorInput();
        IResource resource = null;
        if (input != null) {
            Object o = input.getAdapter(IFile.class);
            if (o instanceof IResource) {
                resource = (IResource) o;
            }
        }
        return resource;
    }

    public void dispose() {
        for (IMarker marker : violationMarkerMap.values()) {
            try {
                marker.delete();
            } catch (CoreException e) {
                logger.error(String.format("Exception occured when removing a marker on dispose: %s", e.getMessage()),
                        e);
            }
        }
        engine.dispose();
    }

    public IMarker getMarker(IPatternMatch match) {
        return this.markerMap.get(match);
    }

    public IMarker addMarker(IPatternMatch match, IMarker marker) {
        return this.markerMap.put(match, marker);
    }

    public IMarker removeMarker(IPatternMatch match) {
        return this.markerMap.remove(match);
    }

    public IMarker getMarker(IViolation violation) {
        return this.violationMarkerMap.get(violation);
    }

    public IMarker addMarker(IViolation violation, IMarker marker) {
        return this.violationMarkerMap.put(violation, marker);
    }

    public IMarker removeMarker(IViolation violation) {
        return this.violationMarkerMap.remove(violation);
    }

    protected IResource getResourceForEditor() {
        return resourceForEditor;
    }
}
