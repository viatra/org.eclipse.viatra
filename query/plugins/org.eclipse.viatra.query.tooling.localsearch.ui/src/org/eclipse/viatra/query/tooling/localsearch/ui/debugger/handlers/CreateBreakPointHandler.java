/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;

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

    public void breakPointHandler(LocalSearchDebugView localSearchDebugView, IStructuredSelection thisSelection) {
        if (thisSelection.size() != 1) {
            // when more than one operation is selected, place no breakpoints
            return;
        }
        
        SearchOperationNode selectedNode = (SearchOperationNode) thisSelection.getFirstElement();
        selectedNode.toggleBreakpoint(!selectedNode.isBreakpointSet());
        localSearchDebugView.getOperationListViewer().refresh(selectedNode);
    }
    
}
