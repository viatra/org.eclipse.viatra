/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * Utility class to serve as an extension for {@link WorkbenchPart}s wishing to use
 * IncQuery Viewers.
 * 
 * @author istvanrath
 *
 */
public abstract class IncQueryViewersPartSupport {

	/**
	 * The "owner" of this support instance.
	 */
	protected IWorkbenchPart owner;
	
	/**
	 * Constructs a new support instance.
	 */
	public IncQueryViewersPartSupport(IWorkbenchPart _owner, ViewersComponentConfiguration _config) {
		this.owner = _owner;
		this.configuration = _config;
	}
	
	/**
	 * Initialize the support instance.
	 * Should be called when the owner {@link IWorkbenchPart} has finished initializing itself
	 * (typically at the end of its init() method).
	 */
	public void init() {
		this.owner.getSite().getPage().addSelectionListener(forwardRevealListener);
	}
	
	/**
	 * Dispose the support instance.
	 * Should be called when the owner {@link IWorkbenchPart} it starting to dispose itself.
	 */
	public void dispose() {
		this.owner.getSite().getPage().removeSelectionListener(forwardRevealListener);
	}
	
	/**
	 * The configuration DTO.
	 */
	public ViewersComponentConfiguration configuration;
	
	/**
     * Selection listener for the "forward reveal" feature.
     */
    private final ISelectionListener forwardRevealListener = new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            if (!owner.equals(part) && selection instanceof IStructuredSelection) {
                ArrayList<Notifier> r = new ArrayList<Notifier>();
                for (Object _target : ((IStructuredSelection) selection).toArray()) {
                    if (_target instanceof Notifier) {
                        r.add((Notifier) _target);
                    }
                }
                filteredSelectionChanged(r);
//                if (owner instanceof ISelectionProvider) {
//                	((ISelectionProvider)owner).setSelection(new StructuredSelection(r));
//                }
            }
        }
    };
	
    protected abstract void filteredSelectionChanged(List<Notifier> eObjects);
    
}
