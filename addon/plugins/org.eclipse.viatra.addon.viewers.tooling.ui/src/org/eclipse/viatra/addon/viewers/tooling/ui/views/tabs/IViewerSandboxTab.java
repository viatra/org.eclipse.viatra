/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;

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
     */
    void bindState(ViewerState state);
    
    /**
     * Receive focus.
     */
    void setFocus();
    
    /**
     * A list of items that this tab will contribute to the dropdown menu of the Sandbox view.
     */
    List<IContributionItem> getDropDownMenuContributions();
    
    /**
     * A list of items that this tab will contribute to the toolbar of the Sandbox view.
     */
    List<IContributionItem> getToolBarContributions();
    
    /**
     * Called when the UI is being disposed.
     */
    void dispose();
    
}
