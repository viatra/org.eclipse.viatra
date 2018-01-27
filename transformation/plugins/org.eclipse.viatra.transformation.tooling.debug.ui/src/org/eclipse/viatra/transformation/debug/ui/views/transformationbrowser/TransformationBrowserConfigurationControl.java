/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;

public class TransformationBrowserConfigurationControl extends WorkbenchWindowControlContribution{

    public TransformationBrowserConfigurationControl() {
        super();
    }

    public TransformationBrowserConfigurationControl(String id) {
        super(id);
    }

    private TransformationBrowserView getAdaptableTransformationBrowser(){
        try {
            TransformationBrowserView view = (TransformationBrowserView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TransformationBrowserView.ID);
            return view;
        } catch (PartInitException e) {
            TransformationDebugUIActivator.getDefault().logException(e.getMessage(), e);
        }
        return null;
    }
    
    private void applyConfligurationSelection(TransformationViewConfiguration config){
        TransformationBrowserView view = getAdaptableTransformationBrowser();
        if(view != null){
            view.setViewConfiguration(config);
        }
    }

    
    @Override
    protected Control createControl(Composite parent) {
        final ComboViewer viewer = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText(Object element) {
                if (element instanceof TransformationViewConfiguration){
                    return element.toString();
                }
                return super.getText(element);
            }
        });
        
        
        viewer.setInput(TransformationViewConfiguration.values());
        viewer.setSelection(new StructuredSelection(TransformationViewConfiguration.RULE_BROWSER));
        applyConfligurationSelection(TransformationViewConfiguration.RULE_BROWSER);
        
        viewer.addSelectionChangedListener(event -> {
            final ISelection select = event.getSelection();
            if (select instanceof IStructuredSelection){
                IStructuredSelection selection = (IStructuredSelection) select;
                Object o = selection.getFirstElement();
                if (o instanceof TransformationViewConfiguration){
                    applyConfligurationSelection((TransformationViewConfiguration) o);
                }
            }
        });
        viewer.getControl().setToolTipText("Select the displayed information regarding running VIATRA transformations");
        
        return viewer.getControl();
    }

}