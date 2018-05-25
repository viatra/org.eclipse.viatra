/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.zest;

import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestContentProvider;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.viatra.integration.zest.viewer.ModifiableZestContentViewer;

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
     * The basic bindings does not support isolated nodes but is more
     * performant. If the graph contains isolated nodes, use
     * {@link #bindWithIsolatedNodes(GraphViewer, ViewerState)} instead.
     */
    public static void bind(ModifiableZestContentViewer viewer, ViewerState state) {
        if (!(viewer.getContentProvider() instanceof ZestContentProvider)) {
            viewer.setContentProvider(new ZestContentProvider());
        }
        
        if (!(viewer.getLabelProvider() instanceof ZestLabelProvider)) {
            viewer.setLabelProvider(new ZestLabelProvider());
        }
        viewer.setInput(state);
    }

    /**
     * The basic bindings does not support isolated nodes but is more
     * performant. If the graph contains isolated nodes, use
     * {@link #bindWithIsolatedNodes(GraphViewer, ViewerState, boolean)} instead.
     */
    public static void bind(ModifiableZestContentViewer viewer, ViewerState state,
            boolean displayContainment) {
        if (!(viewer.getContentProvider() instanceof ZestContentProvider)) {
            viewer.setContentProvider(new ZestContentProvider(displayContainment));
        }
        
        if (!(viewer.getLabelProvider() instanceof ZestLabelProvider)) {
            viewer.setLabelProvider(new ZestLabelProvider());
        }
        viewer.setInput(state);
    }



}
