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
package org.eclipse.incquery.querybasedui.runtime.zest;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.incquery.querybasedui.runtime.model.ViewerDataModel;
import org.eclipse.incquery.querybasedui.runtime.zest.sources.ZestContentProvider;
import org.eclipse.incquery.querybasedui.runtime.zest.sources.ZestLabelProvider;

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
        viewer.setContentProvider(new ZestContentProvider());
        viewer.setLabelProvider(new ZestLabelProvider());
        viewer.setInput(model);
    }

}
