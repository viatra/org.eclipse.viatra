/*******************************************************************************
 * Copyright (c) 2010-2013, Csaba Debreceni, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.sources.ListContentProvider;
import org.eclipse.viatra.addon.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.viatra.addon.viewers.runtime.sources.TreeContentProvider;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class ViatraViewerSupport {

    private ViatraViewerSupport() {}
    
    /**
     * Bind the viewerstate to the list viewer.
     * @param viewer
     * @param state
     */
    public static void bind(AbstractListViewer viewer, ViewerState state) {
        // this seems to be necessary to avoid a databinding-related exception 
        // which comes when the viewer already had some contents before the current run		
        if (viewer.getInput()!=null) {
            viewer.setInput(null);
        }
        
        if (!(viewer.getContentProvider() instanceof ListContentProvider)) {
            viewer.setContentProvider(new ListContentProvider());
        }

        
        if (!(viewer.getLabelProvider() instanceof QueryLabelProvider)) {
            viewer.setLabelProvider(new QueryLabelProvider());
        }

        viewer.setInput(state);			
        viewer.refresh();
    }

    /**
     * Bind the viewerstate to the tree viewer.
     * @param viewer
     * @param state
     */
    public static void bind(AbstractTreeViewer viewer, ViewerState state) {
        // this seems to be necessary to avoid a databinding-related exception 
        // which comes when the viewer already had some contents before the current run
        if (viewer.getInput()!=null) {
            viewer.setInput(null);
        }
        
        if (!(viewer.getContentProvider() instanceof TreeContentProvider)) {
            viewer.setContentProvider(new TreeContentProvider());
        }
        
        if (!(viewer.getLabelProvider() instanceof QueryLabelProvider)) {
            viewer.setLabelProvider(new QueryLabelProvider());
        }
                
        viewer.setInput(state);	
        viewer.refresh();
    }
}
