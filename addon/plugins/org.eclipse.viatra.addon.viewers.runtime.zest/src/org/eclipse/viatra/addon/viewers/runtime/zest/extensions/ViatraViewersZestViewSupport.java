/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.zest.extensions;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.addon.viewers.runtime.extensions.jface.ViatraViewersJFaceViewSupport;
import org.eclipse.viatra.addon.viewers.runtime.model.ViatraViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViewersZestPlugin;
import org.eclipse.viatra.integration.zest.viewer.ViatraGraphViewer;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViatraGraphViewers;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * Support class for {@link ViewPart}s based on a single Zest {@link GraphViewer}.
 * @author istvanrath
 *
 */
public class ViatraViewersZestViewSupport extends
        ViatraViewersJFaceViewSupport {

    private final ViatraGraphViewer graphViewer;
    
    public GraphViewer getGraphViewer() {
        return graphViewer;
    }
    
    public ViatraViewersZestViewSupport(
            IViewPart _owner,
            ViewersComponentConfiguration _config,
            IModelConnectorTypeEnum _scope,
            ViatraGraphViewer _graphViewer) {
        super(_owner, _config, _scope, _graphViewer);
        this.graphViewer = _graphViewer;
    }

    @Override
    protected void init() {
        super.init();
        //this.getGraphViewer().setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        this.getGraphViewer().setLayoutAlgorithm(new RadialLayoutAlgorithm());
    }
    
    @Override
    protected void bindModel() {
        Assert.isNotNull(this.configuration);
        Assert.isNotNull(this.configuration.getPatterns());
        
        if (state!=null && !state.isDisposed()) {
            state.dispose();
        }
        ViatraQueryEngine engine = getEngine();
        if (engine!=null) {
            state = ViatraViewerDataModel.newViewerState(
                    engine, 
                    this.configuration.getPatterns(), 
                    this.configuration.getFilter(),  
                    Arrays.asList(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
            ViatraGraphViewers.bind((graphViewer), state);
        }
    }
    
    public void createToolbar() {
        refreshGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID, "icons/refresh.gif"));
        clearGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID, "icons/clear.gif"));

        IToolBarManager toolBarManager = getOwner().getViewSite().getActionBars().getToolBarManager();
        toolBarManager.removeAll();
        toolBarManager.add(refreshGraph);
        toolBarManager.add(clearGraph);
//        if (owner instanceof IZoomableWorkbenchPart) {
//	        ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem((IZoomableWorkbenchPart)owner);
//	        toolBarManager.add(toolbarZoomContributionViewItem);
//	        toolBarManager.update(true);
//        }
        IMenuManager menuManager = getOwner().getViewSite().getActionBars().getMenuManager();
        menuManager.removeAll();
        menuManager.add(createLayoutMenu());
    }

    public MenuManager createLayoutMenu() {
        MenuManager mgr = new MenuManager("Layout");
        mgr.add(createLayoutAction("Tree", new TreeLayoutAlgorithm()));
        mgr.add(createLayoutAction("Spring", new SpringLayoutAlgorithm()));
        mgr.add(createLayoutAction("Radial", new RadialLayoutAlgorithm()));
        return mgr;
    }

    protected Action refreshGraph = new Action("Refresh Graph") {
        @Override
        public void run() {
            getGraphViewer().applyLayout();
            getGraphViewer().refresh();
        }
    };

    protected Action clearGraph = new Action("Clear Graph") {
        @Override
        public void run() {
            final GraphViewer viewer = getGraphViewer();
            if (viewer.getContentProvider() != null && viewer.getInput() != null) {
                viewer.setInput(null);
            }
        }
    };

    protected Action createLayoutAction(final String name, final LayoutAlgorithm lay) {
        return new Action(name) {
            @Override
            public void run() {
                getGraphViewer().setLayoutAlgorithm(lay);
                getGraphViewer().applyLayout();
            }
        };
    }

}
