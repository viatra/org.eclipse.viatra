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

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestContentProvider;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestContentWithIsolatedNodesProvider;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestLabelProvider;

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
	 * 
	 * @deprecated Use {@link #bind(GraphViewer, ViewerStateSet)} where
	 *             {@link ViewerStateSet} consists of the shared data between
	 *             various viewers.
	 */
//	public static void bind(GraphViewer viewer, ViewerDataModel model) {
//		bind(viewer, model, ViewerDataFilter.UNFILTERED);
//	}

	/**
	 * 
	 * @deprecated Use {@link #bind(GraphViewer, ViewerStateSet)} where
	 *             {@link ViewerStateSet} consists of the shared data between
	 *             various viewers.
	 */
//	public static void bind(GraphViewer viewer, ViewerDataModel model,
//			ViewerDataFilter filter) {
//		ViewerState state = ViewerState.newInstance(model, filter,
//				ImmutableSet.of(ViewerStateFeature.EDGE));
//		bind(viewer, state);
//	}

	/**
	 * The basic bindings does not support isolated nodes but is more
	 * performant. If the graph contains isolated nodes, use
	 * {@link #bindWithIsolatedNodes(GraphViewer, ViewerState)} instead.
	 */
	public static void bind(GraphViewer viewer, ViewerState state) {
		viewer.setContentProvider(new ZestContentProvider());
		viewer.setLabelProvider(new ZestLabelProvider(state, viewer
				.getControl().getDisplay()));
		viewer.setInput(state);
	}

	/**
	 * The basic bindings does not support isolated nodes but is more
	 * performant. If the graph contains isolated nodes, use
	 * {@link #bindWithIsolatedNodes(GraphViewer, ViewerState, boolean)} instead.
	 */
	public static void bind(GraphViewer viewer, ViewerState state,
			boolean displayContainment) {
		viewer.setContentProvider(new ZestContentProvider(displayContainment));
		viewer.setLabelProvider(new ZestLabelProvider(state, viewer
				.getControl().getDisplay()));
		viewer.setInput(state);
	}

	public static void bindWithIsolatedNodes(GraphViewer viewer,
			ViewerState state) {
		viewer.setContentProvider(new ZestContentWithIsolatedNodesProvider());
		viewer.setLabelProvider(new ZestLabelProvider(state, viewer
				.getControl().getDisplay()));
		viewer.setInput(state);
	}

	public static void bindWithIsolatedNodes(GraphViewer viewer,
			ViewerState state, boolean displayContainment) {
		viewer.setContentProvider(new ZestContentWithIsolatedNodesProvider(
				displayContainment));
		viewer.setLabelProvider(new ZestLabelProvider(state, viewer
				.getControl().getDisplay()));
		viewer.setInput(state);
	}

}
