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

import java.util.List;

import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.jface.action.IContributionItem;
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

    /**
     * The tab title.
     */
    String getTabTitle();

    /**
     * Initializes the widget in the given container
     * 
     * @param parent
     */
    void createPartControl(CTabFolder folder);

    /**
     * Binds the content of a viewer state to the current tab
     * 
     * @param model
     * @param filter
     */
    void bindState(ViewerState state, ViewerDataFilter filter);
    
    /**
     * Receive focus.
     */
    void setFocus();
    
    /**
     * A list of items that this tab will contribute to the dropdown menu of the Sandbox view.
     * @return
     */
    List<IContributionItem> getDropDownMenuContributions();
    
    /**
     * A list of items that this tab will contribute to the toolbar of the Sandbox view.
     * @return
     */
    List<IContributionItem> getToolBarContributions();
}
