package org.eclipse.incquery.viewers.tooling.ui.views.tabs;

import org.eclipse.incquery.viewers.runtime.IncQueryViewerSupport;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;

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
    public void bindModel(ViewerDataModel model) {
        IncQueryViewerSupport.bind(viewer, model);

    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();

    }

}
