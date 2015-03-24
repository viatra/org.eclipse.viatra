package org.eclipse.incquery.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class CreateBreakPointHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			LocalSearchDebugView localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
			ISelection selection = localSearchDebugView.getOperationListViewer().getSelection();
			breakPointHandler((IStructuredSelection) selection);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static void breakPointHandler(IStructuredSelection thisSelection) {
		if(thisSelection.size() > 1){
            // when more than one operation is selected, place no breakpoints
            return;
        }
        
        SearchOperationViewerNode selectedNode = (SearchOperationViewerNode) thisSelection.getFirstElement();
        
        selectedNode.setBreakpoint(!selectedNode.isBreakpoint());
        
        LocalSearchDebugView localSearchDebugView;
		try {
			localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
			LocalSearchDebugger debugger = localSearchDebugView.getDebugger();
			
			debugger.getLocalSearchDebugView().refreshView();
			debugger.getLocalSearchDebugView().getOperationListViewer().setSelection(null);
		} catch (PartInitException e) {
			// TODO proper logging
			e.printStackTrace();
		}
	}
	
}
