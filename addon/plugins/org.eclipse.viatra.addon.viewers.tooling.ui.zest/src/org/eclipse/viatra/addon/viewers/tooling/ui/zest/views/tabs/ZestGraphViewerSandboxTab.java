/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.zest.views.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViatraGraphViewers;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViewersZestPlugin;
import org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs.AbstractViewerSandboxTab;
import org.eclipse.viatra.integration.zest.viewer.ViatraGraphViewer;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

public class ZestGraphViewerSandboxTab extends AbstractViewerSandboxTab implements IZoomableWorkbenchPart {

    ViatraGraphViewer viewer;

    @Override
    public String getTabTitle() {
        return "Zest Graph";
    }

    @Override
    public void bindState(ViewerState state) {
        ViatraGraphViewers.bind(viewer, state, true);
//        ViatraGraphViewers.bindWithIsolatedNodes(viewer, state, true);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    protected Viewer getViewer() {
        return viewer;
    }

    @Override
    protected Viewer createViewer(Composite parent) {
        viewer = new ViatraGraphViewer(parent, SWT.NONE);
        viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
        viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
        
        refreshGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID,"icons/refresh.gif"));
        clearGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID,"icons/clear.gif"));

        
        return viewer;
    }
    
    private MenuManager createLayoutMenu() {
        MenuManager mgr = new MenuManager("Layout");
        mgr.add(createLayoutAction("Tree", new TreeLayoutAlgorithm()));
        mgr.add(createLayoutAction("Spring", new SpringLayoutAlgorithm()));
        mgr.add(createLayoutAction("Radial", new RadialLayoutAlgorithm()));
        return mgr;
    }
    
    private Action createLayoutAction(final String name, final LayoutAlgorithm lay) {
        return new Action(name) {
          @Override
          public void run() {
            viewer.setLayoutAlgorithm(lay);
            viewer.refresh();
          }  
        };
    }

    @Override
    public List<IContributionItem> getDropDownMenuContributions() {
        List<IContributionItem> r = new ArrayList<IContributionItem>();
        r.add(createLayoutMenu());
        return r;
    }

    private Action refreshGraph = new Action("Refresh Graph") {
        @Override
        public void run() {
            viewer.refresh();
        }
    };
    
    private Action clearGraph = new Action("Clear Graph") {
        @Override
        public void run() {
            viewer.setInput(null);
        }
    };
    
    
    @Override
    public List<IContributionItem> getToolBarContributions() {
        List<IContributionItem> r = new ArrayList<IContributionItem>();
        MenuManager mgr = new MenuManager();
        mgr.removeAll();
        mgr.add(refreshGraph);
        mgr.add(clearGraph);

        ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
        mgr.add(toolbarZoomContributionViewItem);
        
        mgr.update(true);
        r.add(mgr);
        return r;
    }

    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return viewer;
    }

}
