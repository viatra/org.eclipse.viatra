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

package org.eclipse.viatra.addon.validation.runtime;

import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.addon.validation.core.ValidationEngine;
import org.eclipse.viatra.addon.validation.core.api.IConstraint;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.addon.validation.core.api.IValidationEngine;
import org.eclipse.viatra.addon.validation.core.listeners.ConstraintListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

/**
 * The constraint adapter class is used to collect the constraints and deal with their maintenance for a given EMF
 * instance model. If the validation framework is initialized an instance of this class will be created which handles
 * the creation of the appropriate rules and their jobs.
 * 
 * @author Tamas Szabo
 */
public class ConstraintAdapter {

    private final IResource resourceForEditor;
    private final IValidationEngine engine;
    private final ConstraintListener constraintListener; 

    public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
        this(getIResourceForEditor(editorPart),
                ConstraintExtensionRegistry.getConstraintSpecificationsForEditorId(editorPart.getSite().getId()),
                new MarkerManagerViolationListener(getIResourceForEditor(editorPart), logger),
                notifier, logger);
    }
    
    /**
     * @since 2.1
     */
    public ConstraintAdapter(IResource resourceForEditor, Set<IConstraintSpecification> constraintSpecificationsForEditorId, ConstraintListener constraintListener, Notifier notifier, Logger logger) {
        this.resourceForEditor = resourceForEditor;
        ViatraQueryEngine queryEngine = ViatraQueryEngine.on(new EMFScope(notifier));
        engine = ValidationEngine.builder().setEngine(queryEngine).setLogger(logger).build();
        engine.initialize();
        
        this.constraintListener = Objects.requireNonNull(constraintListener);
        for (IConstraintSpecification constraint : constraintSpecificationsForEditorId) {
            IConstraint coreConstraint = engine.addConstraintSpecification(constraint);
            coreConstraint.addListener(this.constraintListener);
        }
    }

    private static IResource getIResourceForEditor(IEditorPart editorPart) {
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
        constraintListener.dispose();
        engine.dispose();
    }

    protected IResource getResourceForEditor() {
        return resourceForEditor;
    }
}
