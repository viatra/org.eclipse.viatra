package org.eclipse.incquery.viewers.tooling.ui.views.tabs;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractViewerSandboxTab implements IViewerSandboxTab {

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        getViewer().removeSelectionChangedListener(listener);
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        getViewer().addSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        StructuredViewer viewer = getViewer();
        if (viewer != null && !(viewer.getControl().isDisposed())) {
            viewer.setSelection(selection);
        }
    }

    @Override
    public ISelection getSelection() {
        StructuredViewer viewer = getViewer();
        if (viewer != null && !(viewer.getControl().isDisposed())) {
            return viewer.getSelection();
        } else {
            return StructuredSelection.EMPTY;
        }
    }

    @Override
    public void createPartControl(CTabFolder folder) {
        CTabItem tab = new CTabItem(folder, SWT.NONE);
        tab.setText(getTabTitle());
        StructuredViewer viewer = createViewer(folder);
        tab.setControl(viewer.getControl());

    }

    protected abstract StructuredViewer getViewer();
    protected abstract StructuredViewer createViewer(Composite parent);
}
