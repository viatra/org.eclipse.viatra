/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.extensions;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * Utility class to serve as an extension for {@link WorkbenchPart}s wishing to use
 * VIATRA Viewers.
 * 
 * Supports:
 *  - "forward reveal" mode, i.e. listen to global selection changes and attempt to show the corresponding
 *  contents inside the owner (through the callback "filteredSelectionChanged")
 * 
 * @author istvanrath
 *
 */
public abstract class ViatraViewersPartSupport {

    /**
     * The "owner" of this support instance.
     */
    protected IWorkbenchPart owner;
    protected boolean disposed;
    
    /**
     * Constructs a new support instance.
     */
    public ViatraViewersPartSupport(IWorkbenchPart _owner, ViewersComponentConfiguration _config) {
        this.owner = _owner;
        this.configuration = _config;
        this.disposed = false;
    }
    
    /**
     * Initialize the support instance.
     */
    protected void init() {
        this.owner.getSite().getPage().addSelectionListener(forwardRevealListener);
    }
    
    /**
     * Dispose the support instance.
     * Should be called when the owner {@link IWorkbenchPart} it starting to dispose itself.
     */
    public void dispose() {
        this.disposed = true;
        this.owner.getSite().getPage().removeSelectionListener(forwardRevealListener);
    }
    
    /**
     * The configuration DTO.
     */
    protected ViewersComponentConfiguration configuration;
    
    /**
     * Selection listener for the "forward reveal" feature.
     */
    private final ISelectionListener forwardRevealListener = (part, selection) -> {
        if (!owner.equals(part) && selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            onSelectionChanged(structuredSelection.toList());
        }
    };
    
    protected abstract void onSelectionChanged(List<Object> object);
    
}
