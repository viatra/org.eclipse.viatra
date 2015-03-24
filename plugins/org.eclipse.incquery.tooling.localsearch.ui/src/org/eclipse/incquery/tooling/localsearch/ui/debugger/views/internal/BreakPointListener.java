/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.localsearch.ui.debugger.views.internal;

import org.eclipse.incquery.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Marton Bur
 *
 */
public class BreakPointListener implements IDoubleClickListener {

    private LocalSearchDebugger localSearchDebugger;
    
    public BreakPointListener(LocalSearchDebugger localSearchDebugger){
        this.localSearchDebugger = localSearchDebugger;
    }
    
    @Override
	public void doubleClick(DoubleClickEvent event) {
		// TreeViewer viewer = (TreeViewer) event.getViewer();
		IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();

        if(thisSelection.size() > 1){
            // when more than one operation is selected, place no breakpoints
            return;
        }
        
        SearchOperationViewerNode selectedNode = (SearchOperationViewerNode) thisSelection.getFirstElement();
        
        selectedNode.setBreakpoint(!selectedNode.isBreakpoint());
        
        localSearchDebugger.getLocalSearchDebugView().refreshView();
        localSearchDebugger.getLocalSearchDebugView().getOperationListViewer().setSelection(null);
        
    }

}
