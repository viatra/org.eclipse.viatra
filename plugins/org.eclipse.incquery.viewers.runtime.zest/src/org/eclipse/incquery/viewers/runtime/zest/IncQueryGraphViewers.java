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
package org.eclipse.incquery.viewers.runtime.zest;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.incquery.viewers.runtime.zest.sources.ZestContentProvider;
import org.eclipse.incquery.viewers.runtime.zest.sources.ZestLabelProvider;

import com.google.common.collect.ImmutableSet;

/**
 * API to bind the result of model queries to Zest {@link GraphViewer} widgets.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IncQueryGraphViewers {

    private IncQueryGraphViewers() {
    }

    /**
     * 
     * @deprecated Use {@link #bind(GraphViewer, ViewerStateSet)} where
     *             {@link ViewerStateSet} consists of the shared data between
     *             various viewers.
     */
    public static void bind(GraphViewer viewer, ViewerDataModel model) {
        bind(viewer, model, ViewerDataFilter.UNFILTERED);
    }

    /**
     * 
	 * @deprecated Use {@link #bind(GraphViewer, ViewerStateSet)} where
	 *             {@link ViewerStateSet} consists of the shared data between
	 *             various viewers.
     */
    public static void bind(GraphViewer viewer, ViewerDataModel model, ViewerDataFilter filter) {
    	ViewerState state = ViewerState.newInstance(model, filter, ImmutableSet.of(ViewerStateFeature.EDGE));
        bind(viewer, state);
    }

	public static void bind(GraphViewer viewer, ViewerState state) {
		viewer.setContentProvider(new ZestContentProvider());
        viewer.setLabelProvider(new ZestLabelProvider(viewer.getControl().getDisplay()));
		viewer.setInput(state);
	}
	
	public static void bind(GraphViewer viewer, ViewerState state, boolean displayContainment) {
		viewer.setContentProvider(new ZestContentProvider(displayContainment));
		viewer.setLabelProvider(new ZestLabelProvider(viewer.getControl().getDisplay()));
		viewer.setInput(state);
	}

}
