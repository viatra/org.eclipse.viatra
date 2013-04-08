package org.eclipse.incquery.viewers.tooling.ui.zest.views.tabs;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.incquery.viewers.runtime.zest.IncQueryGraphViewers;
import org.eclipse.incquery.viewers.tooling.ui.views.tabs.AbstractViewerSandboxTab;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ZestGraphViewerSandboxTab extends AbstractViewerSandboxTab {

    GraphViewer viewer;

    @Override
    public String getTabTitle() {
        return "Graph";
    }

    @Override
    public void bindModel(ViewerDataModel model) {
        IncQueryGraphViewers.bind(viewer, model);
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
        LayoutAlgorithm layout = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.BOTTOM_UP);
        viewer.setLayoutAlgorithm(layout, true);
        return viewer;
    }

}
