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

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.addon.viewers.runtime.extensions.jface.IncQueryViewersJFaceViewSupport;
import org.eclipse.viatra.addon.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViewersZestPlugin;
import org.eclipse.viatra.addon.viewers.runtime.zest.IncQueryGraphViewers;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;

import com.google.common.collect.ImmutableSet;

/**
 * Support class for {@link ViewPart}s based on a single Zest {@link GraphViewer}.
 * @author istvanrath
 *
 */
public class IncQueryViewersZestViewSupport extends
		IncQueryViewersJFaceViewSupport {

	private final GraphViewer graphViewer;
	
    public GraphViewer getGraphViewer() {
        return graphViewer;
    }
    
	public IncQueryViewersZestViewSupport(
			IViewPart _owner,
			ViewersComponentConfiguration _config,
			IModelConnectorTypeEnum _scope,
			GraphViewer _graphViewer) {
		super(_owner, _config, _scope, _graphViewer);
		this.graphViewer = _graphViewer;
	}

	@Override
	protected void init() {
		super.init();
		this.getGraphViewer().setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		this.getGraphViewer().setLayoutAlgorithm(new RadialLayoutAlgorithm());
	}
	
	@Override
	protected void bindModel() {
		Assert.isNotNull(this.configuration);
		Assert.isNotNull(this.configuration.getPatterns());
		
		if (state!=null && !state.isDisposed()) {
    		state.dispose();
    	}
		IncQueryEngine engine = getEngine();
		if (engine!=null) {
			state = IncQueryViewerDataModel.newViewerState(
					engine, 
	    			this.configuration.getPatterns(), 
	    			this.configuration.getFilter(),  
	    			ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
			IncQueryGraphViewers.bindWithIsolatedNodes(((GraphViewer)jfaceViewer), state);
		}
	}
	
	public void createToolbar() {
        refreshGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID, "icons/refresh.gif"));
        clearGraph.setImageDescriptor(ViewersZestPlugin.imageDescriptorFromPlugin(ViewersZestPlugin.PLUGIN_ID, "icons/clear.gif"));

        IToolBarManager toolBarManager = getOwner().getViewSite().getActionBars().getToolBarManager();
        toolBarManager.removeAll();
        toolBarManager.add(refreshGraph);
        toolBarManager.add(clearGraph);
        if (owner instanceof IZoomableWorkbenchPart) {
	        ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem((IZoomableWorkbenchPart)owner);
	        toolBarManager.add(toolbarZoomContributionViewItem);
	        toolBarManager.update(true);
        }
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
        mgr.add(createLayoutAction("Sugiyama (unstable)", new CompositeLayoutAlgorithm(new LayoutAlgorithm[] {
                sugiyamaAlgorithm, shiftAlgorithm })));
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
            getGraphViewer().setInput(null);
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
