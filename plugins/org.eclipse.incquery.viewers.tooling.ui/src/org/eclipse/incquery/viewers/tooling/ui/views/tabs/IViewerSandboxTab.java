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
package org.eclipse.incquery.viewers.tooling.ui.views.tabs;

import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.CTabFolder;

/**
 * A viewer sandbox tab implementation is used to add contents to the viewers sandbox view. The tab is instantiatable
 * multiple times, each implementation maintains its own state.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IViewerSandboxTab extends ISelectionProvider {

    String getTabTitle();

    /**
     * Initializes the widget in the given container
     * 
     * @param parent
     */
    void createPartControl(CTabFolder folder);

    /**
     * Binds the content of a data model to the current tab
     * 
     * @param model
     * @param filter
     */
    void bindModel(ViewerDataModel model, ViewerDataFilter filter);
    
    void setFocus();
}
