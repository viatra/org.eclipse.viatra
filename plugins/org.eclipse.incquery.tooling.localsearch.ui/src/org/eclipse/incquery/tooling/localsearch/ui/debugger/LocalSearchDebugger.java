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
package org.eclipse.incquery.tooling.localsearch.ui.debugger;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlan;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.SearchPlanView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugger implements ILocalSearchAdapter {

    public static volatile Object notifier = new Object();

    private SearchPlanView searchPlanView;
    
    @Override
    public void patternMatchingStarted() {
        // Handle matching started event here
    }

    @Override
    public void patternMatchingFinished() {
     // Handle matching finished event here
    }


    @Override
    public void planStarted(final SearchPlan plan, int currentOperation) {
        
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    searchPlanView = (SearchPlanView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SearchPlanView.ID);
                    searchPlanView.getOperationListViewer().setInput(plan.getOperations());
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    public void planFinished(SearchPlan plan, int currentOperation) {
        // Handle plan finished event here
    }
 
    @Override
    public void operationSelected(final SearchPlanExecutor planExecutor, final int currentOperation, final MatchingFrame frame) {
        
        if (searchPlanView.isHalted()) {
            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
    
                @Override
                public void run() {
                        searchPlanView.getOperationLabelProvider().setPlanExecutor(planExecutor);
                        // Redraw; stateless implementation
                        searchPlanView.getOperationListViewer().setInput(planExecutor.getSearchPlan().getOperations());
                        GraphViewer graphViewer = searchPlanView.getGraphViewer();
                        graphViewer.setInput(frame);
                }
            });
        }

        if (searchPlanView != null ) {
            ISearchOperation iSearchOperation = planExecutor.getSearchPlan().getOperations().size() <= currentOperation || currentOperation < 0 ? null : planExecutor.getSearchPlan().getOperations().get(currentOperation);
            if(searchPlanView.isBreakpointHit(iSearchOperation)){
                synchronized (notifier) {
                    try {
                        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                searchPlanView.getOperationListViewer().refresh();
                                searchPlanView.getGraphViewer().refresh();
                            }
                        });
                        notifier.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    public void operationExecuted(SearchPlanExecutor planExecutor, int currentOperation, MatchingFrame frame) {
        // Handle operation executed event here
    }

}
