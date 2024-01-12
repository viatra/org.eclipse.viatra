/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.zest;

import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestContentProvider;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.viatra.integration.zest.viewer.ViatraGraphViewer;

/**
 * API to bind the result of model queries to Zest {@link GraphViewer} widgets.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ViatraGraphViewers {

    private ViatraGraphViewers() {
    }

    /**
     * Binds the given ViewerState to the Graph Viewer.
     * </p>
     * Equivalent to calling {@link #bind(ViatraGraphViewer, ViewerState, boolean)} with a false parameter (ignoring the
     * containment references).
     */
    public static void bind(ViatraGraphViewer viewer, ViewerState state) {
        if (!(viewer.getContentProvider() instanceof ZestContentProvider)) {
            viewer.setContentProvider(new ZestContentProvider());
        }
        
        if (!(viewer.getLabelProvider() instanceof ZestLabelProvider)) {
            viewer.setLabelProvider(new ZestLabelProvider(viewer.getControl().getDisplay()));
        }
        viewer.setInput(state);
    }

    /**
     * Binds the given ViewerState to the Graph Viewer.
     * 
     * @param viewer
     *      The graph viewer to display the contents
     * @param state
     *      The Viewer state that will provide the data for the graph visualization
     * @param displayContainment
     *      If true, containments are depicted as edges in the graph, otherwise they are ignored 
     */
    public static void bind(ViatraGraphViewer viewer, ViewerState state,
            boolean displayContainment) {
        if (!(viewer.getContentProvider() instanceof ZestContentProvider)) {
            viewer.setContentProvider(new ZestContentProvider(displayContainment));
        }
        
        if (!(viewer.getLabelProvider() instanceof ZestLabelProvider)) {
            viewer.setLabelProvider(new ZestLabelProvider(viewer.getControl().getDisplay()));
        }
        viewer.setInput(state);
    }



}
