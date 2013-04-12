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
package org.eclipse.incquery.viewers.runtime.model;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class FilteredViewerDataModel {

    ViewerDataModel model;
    ViewerDataFilter filter;

    /**
     * @param model
     * @param filt
     */
    public FilteredViewerDataModel(ViewerDataModel model, ViewerDataFilter filter) {
        super();
        this.model = model;
        this.filter = filter;
    }

    public ViewerDataModel getModel() {
        return model;
    }

    public ViewerDataFilter getFilter() {
        return filter;
    }

}
