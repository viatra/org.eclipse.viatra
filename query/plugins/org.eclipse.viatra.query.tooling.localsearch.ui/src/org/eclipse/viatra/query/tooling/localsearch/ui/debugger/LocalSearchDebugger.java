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
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Level;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdaptable;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchPlanViewModel;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal.BreakPointListener;

/**
 * An adapter implementation for local search matchers to support debugging
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugger implements ILocalSearchAdapter {

    public static volatile Object notifier = new Object();
    private LocalSearchDebugView localSearchDebugView;
    private Deque<LocalSearchMatcher> runningMatchers;
    private Deque<SearchPlan> runningExecutors;
    private List<ILocalSearchAdaptable> adaptedElements = new ArrayList<>();
    private boolean startHandlerCalled = false;
    private boolean isDisposed = false;
    private boolean hasFinished = false;

    private boolean halted = true;
    private SearchPlanViewModel viewModel;

    
    public LocalSearchDebugView getLocalSearchDebugView() throws PartInitException {
        if (localSearchDebugView == null) {
            localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
        }
        return localSearchDebugView;
    }

    public boolean isStartHandlerCalled() {
        return startHandlerCalled;
    }

    public void setStartHandlerCalled(boolean startHandlerCalled) {
        this.startHandlerCalled = startHandlerCalled;
    }

    @Override
    public void patternMatchingStarted(final LocalSearchMatcher lsMatcher) {
        if (isDisposed) {
            return;
        }
        
        // If a new debug session is starting, obtain the view
        if (startHandlerCalled) {
            startHandlerCalled = false;
            // Syncexec is assumed to be needed because of the showView call
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
                try {
                    getLocalSearchDebugView();
                    BreakPointListener breakPointListener = new BreakPointListener(LocalSearchDebugger.this);
                    TreeViewer operationListViewer = localSearchDebugView.getOperationListViewer();
                    operationListViewer.addDoubleClickListener(breakPointListener);
                    localSearchDebugView.setDebugger(LocalSearchDebugger.this);

                    // TODO make sure that the initialization is done for every part so that restart is possible

                    runningMatchers = new ArrayDeque<>();
                    runningExecutors = new ArrayDeque<>();

                    TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(lsMatcher.getQuerySpecification());
                    @SuppressWarnings("unchecked")
                    List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
                    storedFrames.clear();
                    
                    localSearchDebugView.refreshView();
                } catch (PartInitException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().log(
                            Level.ERROR,
                            "A part init exception occured while executing pattern matcher started handler"
                                    + e.getMessage(), e);
                }
            });
        }
        
        runningMatchers.push(lsMatcher);
    }

    private boolean shouldSelectOtherTab = false; 
    
    @Override
    public void noMoreMatchesAvailable(LocalSearchMatcher matcher) {
        if (isDisposed) {
            return;
        }
        LocalSearchMatcher removedMatcher = runningMatchers.pop();
        if (runningMatchers.isEmpty()) {
            // After all the matching process finished set to halted in order to
            // be able to start a new debug session
            halted = true;
            hasFinished = true;
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
                localSearchDebugView.getMatchesTabFolder().setSelection(0);
                localSearchDebugView.getOperationListViewer().collapseAll();
            });
            localSearchDebugView.refreshView();
        } else {
            // clear frame table
            TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(removedMatcher.getQuerySpecification());
            @SuppressWarnings("unchecked")
            List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
            storedFrames.clear();
            shouldSelectOtherTab = true;
        }
    }

    @Override
    public void planChanged(Optional<SearchPlan> oldPlanOptional, Optional<SearchPlan> newPlanOptional) {
        if (isDisposed) {
            return;
        }
        oldPlanOptional.ifPresent(plan -> runningExecutors.pop());
        newPlanOptional.ifPresent(plan -> runningExecutors.push(plan));

        SearchPlan newPlan = newPlanOptional.orElse(null);
//		final List<SearchOperationViewerNode> viewNodes = createOperationsListFromExecutor(newPlanExecutor);
        if (runningMatchers.size() == 1 && newPlan != null) {
            this.viewModel = new SearchPlanViewModel(createOperationsListFromExecutor(newPlan));
            this.viewModel.setDebugger(this);
            // Set the input when the top level matcher goes to the next plan
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> localSearchDebugView.getOperationListViewer().setInput(viewModel));
        } else if(newPlan != null) {
            viewModel.insertForCurrent(createOperationsListFromExecutor(newPlan));
        }

        // Manage tabs for matching frames
        PQuery querySpecification = runningMatchers.peek().getQuerySpecification();
        final int keySize = querySpecification.getParameters().size();
        final TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(querySpecification);
        @SuppressWarnings("unchecked")
        List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
        if (!storedFrames.isEmpty()) {
            storedFrames.remove(storedFrames.size() - 1);
        }
        // TODO optimize: should not refresh on every plan change only when halted
        PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
            Map<Integer, PVariable> variableMapping = newPlan.getVariableMapping();
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < variableMapping.size(); i++) {
                columnNames.add(variableMapping.get(i).getName());
            }
            localSearchDebugView.recreateColumns(columnNames, keySize, matchesViewer);
        });
    }


    @Override
    public void executorInitializing(SearchPlan searchPlan, MatchingFrame frame) {
        if (isDisposed) {
            return;
        }
        // Add the new frame here to the list of frames. Its contents will change as matching advances
        TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(runningMatchers.peek().getQuerySpecification());
        @SuppressWarnings("unchecked")
        List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
        if (!storedFrames.contains(frame)) {
            storedFrames.add(frame);
        }
    }

    @Override
    public void operationSelected(SearchPlan plan, ISearchOperation operation, MatchingFrame frame) {
        if (isDisposed) {
            return;
        }
        viewModel.stepInto(plan, operation);

        checkForBreakPoint();
    }

    @Override
    public void operationExecuted(SearchPlan plan, ISearchOperation operation, MatchingFrame frame, boolean isSuccessful) {
        if (isDisposed) {
            return;
        }
        if (halted) {
            localSearchDebugView.refreshView();
            if(shouldSelectOtherTab){
                shouldSelectOtherTab = false;
                PlatformUI.getWorkbench().getDisplay().syncExec(() -> localSearchDebugView.getMatchesTabFolder().setSelection(runningMatchers.size()-1));
            }
        }
    }

    @Override
    public void matchFound(SearchPlan planExecutor, MatchingFrame frame) {
        if (isDisposed) {
            return;
        }
        MatchingFrame frameToStore = new MatchingFrame(frame);
        TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(runningMatchers.peek().getQuerySpecification());
        @SuppressWarnings("unchecked")
        List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
        storedFrames.add(storedFrames.size() - 1, frameToStore);
    }

    public void setHalted(boolean halted) {
        this.halted = halted;
    }
    
    public boolean isPatternMatchingRunning() {
        return !hasFinished;
    }
    
    private List<SearchOperationViewerNode> createOperationsListFromExecutor(SearchPlan plan) {
        List<SearchOperationViewerNode> nodes = new ArrayList<>();
        
        for (ISearchOperation operation : plan.getOperations()) {
            nodes.add(new SearchOperationViewerNode(operation, plan));
        }
        // Final "match found" indicator operation
        nodes.add(new SearchOperationViewerNode(plan));
        
        return nodes;
    }
    

    private void checkForBreakPoint() {
        if (localSearchDebugView != null && halted) {
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
                if(shouldSelectOtherTab){
                    shouldSelectOtherTab = false;
                    localSearchDebugView.getMatchesTabFolder().setSelection(runningMatchers.size()-1);
                }
                SearchOperationViewerNode lastSelected = viewModel.getLastSelected();
                localSearchDebugView.getOperationListViewer().collapseAll();
                localSearchDebugView.getOperationListViewer().expandToLevel(lastSelected, 0);
            });
            localSearchDebugView.refreshView();
            synchronized (notifier) {
                try {
                    // Breakpoint hit, wait for notify
                    notifier.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void adapterRegistered(ILocalSearchAdaptable adaptable) {
        if (isDisposed) {
            return;
        }
        adaptedElements.add(adaptable);
    }

    @Override
    public void adapterUnregistered(ILocalSearchAdaptable adaptable) {
        if (isDisposed) {
            return;
        }
        adaptedElements.remove(adaptable);
    }
    
    public void dispose() {
        isDisposed = true;
        for (ILocalSearchAdaptable adaptable : adaptedElements) {
            adaptable.removeAdapter(this);
        }
    }
}
