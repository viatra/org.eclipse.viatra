/*******************************************************************************
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.DisplayUtil;
import org.eclipse.viatra.query.tooling.ui.registry.QueryBackendRegistry;

import com.google.common.collect.Iterables;

/**
 * This Control is used on the {@link QueryExplorer} view's toolbar to enable selection of {@link IQueryBackend} 
 * implementation to be used. This class assumes that a {@link QueryExplorer} instance is always available on the
 * current {@link IWorkbenchWindow}.
 */
public class BackendSelectionControl extends WorkbenchWindowControlContribution {

    
    
    /**
     * 
     */
    public BackendSelectionControl() {
    }

    /**
     * @param id
     */
    public BackendSelectionControl(String id) {
        super(id);
    }

    /**
     * Retrieve the {@link QueryExplorer} instance on the current workbench window. It is assumed that
     * this class is only used on the toolbar of the Query Explorer view, therefore the view is always
     * available.
     * 
     * @return
     */
    private QueryExplorer getQueryExplorer(){
        return QueryExplorer.getInstance(getWorkbenchWindow());
    }
    
    /**
     * Applies the selected backend to the {@link QueryExplorer} instance by updating its {@link QueryEvaluationHint}
     * object. The backend hints are preserved.
     * 
     * @param backend
     */
    private void applyBackendSelection(IQueryBackendFactory backend){
        QueryEvaluationHint oldHint = getQueryExplorer().getHints();
        QueryEvaluationHint newHint = new QueryEvaluationHint(backend, oldHint.getBackendHints());
        getQueryExplorer().setHints(newHint);
    }
    
    private static Iterable<IQueryBackendFactory> getRegisteredQueryBackendImplementations(){
        return QueryBackendRegistry.getInstance().getAllKnownFactories();
    }
    
    @Override
    protected Control createControl(Composite parent) {
        final ComboViewer viewer = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText(Object element) {
                if (element instanceof IQueryBackendFactory){
                    return DisplayUtil.getQueryBackendName((IQueryBackendFactory) element);
                }
                return super.getText(element);
            }
        });
        viewer.setInput(Iterables.toArray(getRegisteredQueryBackendImplementations(), IQueryBackendFactory.class));
        IQueryBackendFactory queryBackendFactory = getQueryExplorer().getHints().getQueryBackendFactory();
		viewer.setSelection(
				queryBackendFactory != null ? new StructuredSelection(queryBackendFactory) : new StructuredSelection());
        
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                final ISelection select = event.getSelection();
                if (select instanceof IStructuredSelection){
                    IStructuredSelection selection = (IStructuredSelection) select;
                    Object o = selection.getFirstElement();
                    if (o instanceof IQueryBackendFactory){
                        applyBackendSelection((IQueryBackendFactory) o);
                    }
                }
            }
        });
        viewer.getControl().setToolTipText("Select query backend engine to be used on subsequent loads.");
        
        return viewer.getControl();
    }

}
