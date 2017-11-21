/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.zest.extensions;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
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
import org.eclipse.viatra.integration.zest.viewer.ModifiableZestContentViewer;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViatraGraphViewers;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;

/**
 * Support class for {@link ViewPart}s based on a single Zest {@link GraphViewer}.
 * @author istvanrath
 *
 */
public class ViatraViewersZestViewSupport extends
        ViatraViewersJFaceViewSupport {

    private final ModifiableZestContentViewer graphViewer;
    
    public ModifiableZestContentViewer getGraphViewer() {
        return graphViewer;
    }
    
    public ViatraViewersZestViewSupport(
            IViewPart _owner,
            ViewersComponentConfiguration _config,
            IModelConnectorTypeEnum _scope,
            ModifiableZestContentViewer _graphViewer) {
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
        mgr.add(createLayoutAction("SpaceTree", new SpaceTreeLayoutAlgorithm()));
        SugiyamaLayoutAlgorithm sugiyamaAlgorithm = new SugiyamaLayoutAlgorithm();
        HorizontalShiftAlgorithm shiftAlgorithm = new HorizontalShiftAlgorithm();
        mgr.add(createLayoutAction("Sugiyama (unstable)", new CompositeLayoutAlgorithm(new ILayoutAlgorithm[] {
                sugiyamaAlgorithm, shiftAlgorithm })));
        return mgr;
    }

    protected Action refreshGraph = new Action("Refresh Graph") {
        @Override
        public void run() {
            getGraphViewer().refresh();
        }
    };

    protected Action clearGraph = new Action("Clear Graph") {
        @Override
        public void run() {
            getGraphViewer().setInput(null);
        }
    };

    protected Action createLayoutAction(final String name, final ILayoutAlgorithm lay) {
        return new Action(name) {
            @Override
            public void run() {
                getGraphViewer().setLayoutAlgorithm(lay);
                getGraphViewer().refresh();
            }
        };
    }

}
