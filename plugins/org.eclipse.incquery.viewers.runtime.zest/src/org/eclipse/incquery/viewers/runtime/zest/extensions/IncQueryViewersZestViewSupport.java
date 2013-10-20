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
package org.eclipse.incquery.viewers.runtime.zest.extensions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.incquery.viewers.runtime.extensions.jface.IncQueryViewersJFaceViewSupport;
import org.eclipse.incquery.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.incquery.viewers.runtime.zest.Activator;
import org.eclipse.incquery.viewers.runtime.zest.IncQueryGraphViewers;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.ImmutableSet;

/**
 * Support class for {@link ViewPart}s based on a single Zest {@link GraphViewer}.
 * @author istvanrath
 *
 */
public class IncQueryViewersZestViewSupport extends
		IncQueryViewersJFaceViewSupport {

	GraphViewer graphViewer;
	
	/**
	 * @param _owner
	 * @param _jfaceViewer
	 */
	public IncQueryViewersZestViewSupport(
			IViewPart _owner,
			ViewersComponentConfiguration _config,
			GraphViewer _graphViewer) {
		super(_owner, _config, _graphViewer);
		this.graphViewer = _graphViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersViewSupport#init()
	 */
	@Override
	public void init() {
		super.init();
		this.graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		this.graphViewer.setLayoutAlgorithm(new RadialLayoutAlgorithm());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersViewSupport#bindModel()
	 */
	@Override
	protected void bindModel() {
		Assert.isNotNull(this.configuration);
		Assert.isNotNull(this.configuration.getPatterns());
		
		if (state!=null && !state.isDisposed()) {
    		state.dispose();
    	}
		IncQueryEngine engine = getEngine(contentsSource);
		if (engine!=null) {
			state = IncQueryViewerDataModel.newViewerState(
					engine, 
	    			this.configuration.getPatterns(), 
	    			this.configuration.getFilter(),  
	    			ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
			IncQueryGraphViewers.bind(((GraphViewer)jfaceViewer), state);
		}
	}
	
	public void createToolbar() {
        refreshGraph.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/refresh.gif"));
        clearGraph.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/clear.gif"));

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
            graphViewer.applyLayout();
            graphViewer.refresh();
        }
    };

    protected Action clearGraph = new Action("Clear Graph") {
        @Override
        public void run() {
            graphViewer.setInput(null);
        }
    };

    protected Action createLayoutAction(final String name, final LayoutAlgorithm lay) {
        return new Action(name) {
            @Override
            public void run() {
                graphViewer.setLayoutAlgorithm(lay);
                graphViewer.applyLayout();
            }
        };
    }
	
}
