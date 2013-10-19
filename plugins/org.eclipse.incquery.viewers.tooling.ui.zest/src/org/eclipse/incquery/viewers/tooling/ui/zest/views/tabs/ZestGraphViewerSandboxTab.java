package org.eclipse.incquery.viewers.tooling.ui.zest.views.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.zest.IncQueryGraphViewers;
import org.eclipse.incquery.viewers.tooling.ui.views.tabs.AbstractViewerSandboxTab;
import org.eclipse.incquery.viewers.tooling.ui.zest.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ZestGraphViewerSandboxTab extends AbstractViewerSandboxTab implements IZoomableWorkbenchPart {

    GraphViewer viewer;

    @Override
    public String getTabTitle() {
        return "Zest Graph";
    }

    @Override
    public void bindState(ViewerState state) {
        IncQueryGraphViewers.bind(viewer, state);//, filter);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    protected StructuredViewer getViewer() {
        return viewer;
    }

    @Override
    protected StructuredViewer createViewer(Composite parent) {
        viewer = new GraphViewer(parent, SWT.None);
        viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        LayoutAlgorithm layout = new CustomSpaceTreeLayoutAlgorithm();
        viewer.setLayoutAlgorithm(layout, true);
        
        refreshGraph.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/refresh.gif"));
        clearGraph.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/clear.gif"));

        
        return viewer;
    }
    
    private MenuManager createLayoutMenu() {
        MenuManager mgr = new MenuManager("Layout");
        mgr.add(createLayoutAction("Tree", new TreeLayoutAlgorithm()));
        mgr.add(createLayoutAction("Spring", new SpringLayoutAlgorithm()));
        mgr.add(createLayoutAction("Radial", new RadialLayoutAlgorithm()));
        mgr.add(createLayoutAction("SpaceTree", new CustomSpaceTreeLayoutAlgorithm()));
        SugiyamaLayoutAlgorithm sugiyamaAlgorithm = new SugiyamaLayoutAlgorithm();
        HorizontalShiftAlgorithm shiftAlgorithm = new HorizontalShiftAlgorithm();
        mgr.add(createLayoutAction("Sugiyama (unstable)",new CompositeLayoutAlgorithm(new LayoutAlgorithm[] {sugiyamaAlgorithm, shiftAlgorithm })));
        return mgr;
    }
    
    private Action createLayoutAction(final String name, final LayoutAlgorithm lay) {
        return new Action(name) {
          @Override
          public void run() {
            viewer.setLayoutAlgorithm(lay);
            redraw();
          }  
        };
    }
    
    private void redraw() {
        if (viewer != null) {
            viewer.applyLayout();
        }
    }

    @Override
    public List<IContributionItem> getDropDownMenuContributions() {
        ArrayList<IContributionItem> r = new ArrayList<IContributionItem>();
        r.add(createLayoutMenu());
        return r;
    }

    private Action refreshGraph = new Action("Refresh Graph") {
        @Override
        public void run() {
            redraw();
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
        ArrayList<IContributionItem> r = new ArrayList<IContributionItem>();
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
