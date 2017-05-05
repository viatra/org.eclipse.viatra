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
package org.eclipse.viatra.addon.viewers.tooling.ui.zest.views.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViatraGraphViewers;
import org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs.AbstractViewerSandboxTab;
import org.eclipse.viatra.integration.zest.viewer.ModifiableZestContentViewer;

public class ZestGraphViewerSandboxTab extends AbstractViewerSandboxTab {//implements IZoomableWorkbenchPart {

    ModifiableZestContentViewer viewer;

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
        viewer = new ModifiableZestContentViewer();
        viewer.createControl(parent, SWT.NONE);
        ILayoutAlgorithm layout = new SpringLayoutAlgorithm();
        viewer.setLayoutAlgorithm(layout);
        
//        refreshGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID,"icons/refresh.gif"));
//        clearGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID,"icons/clear.gif"));

        
        return viewer;
    }
    
    private MenuManager createLayoutMenu() {
        MenuManager mgr = new MenuManager("Layout");
        mgr.add(createLayoutAction("Tree", new TreeLayoutAlgorithm()));
        mgr.add(createLayoutAction("Spring", new SpringLayoutAlgorithm()));
        mgr.add(createLayoutAction("Radial", new RadialLayoutAlgorithm()));
        //mgr.add(createLayoutAction("SpaceTree", new CustomSpaceTreeLayoutAlgorithm()));
        SugiyamaLayoutAlgorithm sugiyamaAlgorithm = new SugiyamaLayoutAlgorithm();
        HorizontalShiftAlgorithm shiftAlgorithm = new HorizontalShiftAlgorithm();
        mgr.add(createLayoutAction("Sugiyama (unstable)",new CompositeLayoutAlgorithm(new ILayoutAlgorithm[] {sugiyamaAlgorithm, shiftAlgorithm })));
        return mgr;
    }
    
    private Action createLayoutAction(final String name, final ILayoutAlgorithm lay) {
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
//    
//    private Action clearGraph = new Action("Clear Graph") {
//        @Override
//        public void run() {
//            viewer.setInput(null);
//        }
//    };
    
    
    @Override
    public List<IContributionItem> getToolBarContributions() {
        List<IContributionItem> r = new ArrayList<IContributionItem>();
        MenuManager mgr = new MenuManager();
        mgr.removeAll();
        mgr.add(refreshGraph);
//        mgr.add(clearGraph);

//        ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
//        mgr.add(toolbarZoomContributionViewItem);
        
        mgr.update(true);
        r.add(mgr);
        return r;
    }

}
