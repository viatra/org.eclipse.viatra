/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class CreateBreakPointHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
		    
			LocalSearchDebugView localSearchDebugView = (LocalSearchDebugView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(LocalSearchDebugView.ID);
			ISelection selection = localSearchDebugView.getOperationListViewer().getSelection();
			breakPointHandler(localSearchDebugView, (IStructuredSelection) selection);
		} catch (PartInitException e) {
			throw new ExecutionException("Error while creating breakpoint", e);
		}
		
		return null;
	}

	public void breakPointHandler(LocalSearchDebugView localSearchDebugView, IStructuredSelection thisSelection) throws PartInitException {
		if(thisSelection.size() > 1){
            // when more than one operation is selected, place no breakpoints
            return;
        }
        
        SearchOperationViewerNode selectedNode = (SearchOperationViewerNode) thisSelection.getFirstElement();
        selectedNode.setBreakpoint(!selectedNode.isBreakpoint());
			
		localSearchDebugView.refreshView();
		localSearchDebugView.getOperationListViewer().setSelection(null);
	}
	
}
