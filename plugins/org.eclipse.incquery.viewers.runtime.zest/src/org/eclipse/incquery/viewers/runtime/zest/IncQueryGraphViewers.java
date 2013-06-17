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

    public static void bind(GraphViewer viewer, ViewerDataModel model) {
        bind(viewer, model, ViewerDataFilter.UNFILTERED);
    }

    public static void bind(GraphViewer viewer, ViewerDataModel model, ViewerDataFilter filter) {
        viewer.setContentProvider(new ZestContentProvider());
        viewer.setLabelProvider(new ZestLabelProvider(viewer.getControl().getDisplay()));
        viewer.setInput(new ViewerState(model, filter, ImmutableSet.of(ViewerStateFeature.EDGE)));
    }

}
