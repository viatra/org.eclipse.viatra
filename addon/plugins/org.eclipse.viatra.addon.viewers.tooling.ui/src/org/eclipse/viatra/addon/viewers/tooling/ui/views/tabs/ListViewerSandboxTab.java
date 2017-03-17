/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.viatra.addon.viewers.runtime.ViatraViewerSupport;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;

public class ListViewerSandboxTab extends AbstractViewerSandboxTab {

    ListViewer viewer = null;

    @Override
    public String getTabTitle() {
        return "List";
    }


    @Override
    protected StructuredViewer getViewer() {
        return viewer;
    }

    @Override
    protected StructuredViewer createViewer(Composite parent) {
        viewer = new ListViewer(parent);
        return viewer;
    }

    @Override
    public void bindState(ViewerState state) {
        ViatraViewerSupport.bind(viewer, state);//, filter);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();

    }


    @Override
    public List<IContributionItem> getDropDownMenuContributions() {
        return null;
    }


    @Override
    public List<IContributionItem> getToolBarContributions() {
        return null;
    }

}
