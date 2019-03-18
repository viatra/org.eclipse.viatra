/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractViewerSandboxTab implements IViewerSandboxTab {
    
    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        getViewer().addSelectionChangedListener(listener);
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        getViewer().removeSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        Viewer viewer = getViewer();
        if (viewer != null && !(viewer.getControl().isDisposed())) {
            try {
                viewer.setSelection(selection);
            } catch (Exception e) {
                // Selection is set on a best-effort basis
            }
        }
    }

    @Override
    public ISelection getSelection() {
        Viewer viewer = getViewer();
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
        Viewer viewer = createViewer(folder);
        tab.setControl(viewer.getControl());
    }
    
    @Override
    public void dispose() {
        // TODO is proper dispose support for jface-based viewers necessary?
        // getViewer().dispose();
    }

    protected abstract Viewer getViewer();
    
    
    protected abstract Viewer createViewer(Composite parent);
}
