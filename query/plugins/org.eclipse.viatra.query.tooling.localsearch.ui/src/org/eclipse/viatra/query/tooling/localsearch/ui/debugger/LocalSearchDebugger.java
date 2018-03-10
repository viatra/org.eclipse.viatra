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
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanDescriptor;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlan;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.ViewModelFactory;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.DebuggerPlanModel;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.IPlanNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.OperationStatus;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.PatternBodyNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal.LocalSearchDebuggerPropertyTester;

/**
 * An adapter implementation for local search matchers to support debugging
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugger implements ILocalSearchAdapter {

    public static volatile Object notifier = new Object();
    private final LocalSearchDebugView localSearchDebugView;
    private Deque<LocalSearchMatcher> runningMatchers;
    private boolean isDisposed = false;
    private boolean hasFinished = false;
    private Deque<IPlanNode> currentOperation;

    private boolean suspended = true;

    private final AdvancedViatraQueryEngine queryEngine;
    private final IQuerySpecification<?> rootSpecification;
    private final Object[] adornment;
    private final ViewModelFactory factory = new ViewModelFactory(this);
    
    private final IPlanNode viewModel;
    
    
    public LocalSearchDebugger(LocalSearchDebugView localSearchDebugView, AdvancedViatraQueryEngine queryEngine, IQuerySpecification<?> rootSpecification, Object[] adornment) {
        this.localSearchDebugView = localSearchDebugView;
        this.queryEngine = queryEngine;
        this.rootSpecification = rootSpecification;
        this.adornment = adornment;
        this.viewModel = getSearchPlan().map(factory::createViewModel).orElse(null);
        
        this.runningMatchers = new ArrayDeque<>();
        this.currentOperation = new ArrayDeque<>();
    }
    
    public AdvancedViatraQueryEngine getQueryEngine() {
        return queryEngine;
    }

    public Optional<IPlanDescriptor> getSearchPlan() {
        Set<PParameter> boundParameters = new HashSet<>(); 
        for (int i = 0; i < adornment.length; i++) {
            if (adornment[i] != null) boundParameters.add(rootSpecification.getParameters().get(i));
        }
        return getSearchPlan(rootSpecification.getInternalQueryRepresentation(), boundParameters);
    }

    public IPlanNode getViewModel() {
        return viewModel;
    }
    
    public Optional<IPlanDescriptor> getSearchPlan(PQuery specification, Set<PParameter> boundParameters) {
        final IQueryBackend lsBackend = queryEngine.getQueryBackend(LocalSearchEMFBackendFactory.INSTANCE);
        if (lsBackend instanceof LocalSearchBackend) {
            LocalSearchBackend localSearchBackend = (LocalSearchBackend) lsBackend;
            return Optional.ofNullable(localSearchBackend.getSearchPlan(specification, boundParameters));
        }
        return Optional.empty();
    }

    @Override
    public void patternMatchingStarted(final LocalSearchMatcher lsMatcher) {
        if (isDisposed) {
            return;
        }
        
        IEvaluationService service = localSearchDebugView.getSite().getService(IEvaluationService.class);
        service.requestEvaluation(LocalSearchDebuggerPropertyTester.DEBUGGER_RUNNING);

        // TODO make sure that the initialization is done for every part so that restart is possible
        TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(lsMatcher.getQuerySpecification());
        @SuppressWarnings("unchecked")
        List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
        storedFrames.clear();
        
        localSearchDebugView.refreshView();
        
        runningMatchers.push(lsMatcher);
    }

    private boolean shouldSelectOtherTab = false; 
    
    private void setCurrentOperation(PBody body) {
        IPlanNode childNode = null;
        List<IPlanNode> nodesToUpdate = new ArrayList<IPlanNode>();
        if (currentOperation.isEmpty()) {
            // Root pattern
            childNode = viewModel.getChildByKey(body);
        } else {
            // Called pattern
            final IPlanNode current = currentOperation.peek();
            if (current instanceof SearchOperationNode && current.isMatcherCall()) {
                childNode = current.getChildByKey(body);
            } else if (current instanceof PatternBodyNode && Objects.equals(((PatternBodyNode) current).getRelatedBody().getPattern(), body.getPattern())) {
                current.setOperationStatus(OperationStatus.EXECUTED);
                currentOperation.pop();
                childNode = currentOperation.peek().getChildByKey(body);
                nodesToUpdate.add(current);
            }
        }
        currentOperation.push(Objects.requireNonNull(childNode, "Child node not found"));
        nodesToUpdate.add(childNode);
        childNode.setOperationStatus(OperationStatus.CURRENT);
        updateNodes(nodesToUpdate);
    }
    
    private void setCurrentOperation(SearchPlan plan, ISearchOperation operation, boolean isBacktrack) {
        IPlanNode node = currentOperation.peek();
        if (node instanceof SearchOperationNode) {
            if (Objects.equals(((PatternBodyNode)node.getParent()).getRelatedBody(), plan.getSourceBody())) {
                currentOperation.pop(); // Remove previous operation
                final IPlanNode childNode = currentOperation.peek().getChildByKey(operation);
                currentOperation.push(childNode);
                updateNodes(Collections.singletonList(childNode));
            } else {
                //TODO body switch!!!
            }
        } else if (node instanceof PatternBodyNode){
            if (Objects.equals(((PatternBodyNode)node).getRelatedBody(), plan.getSourceBody())) {
                // The new operation is still in the current body
                final IPlanNode childNode = node.getChildByKey(operation);
                currentOperation.push(childNode);
                updateNodes(Collections.singletonList(childNode));
            }
        }
    }
    
    private void updateNodes(Collection<IPlanNode> nodes) {
        final TreeViewer viewer = localSearchDebugView.getOperationListViewer();
        PlatformUI.getWorkbench().getDisplay().syncExec(() -> nodes.forEach(viewer::refresh));
    }
    
    @Override
    public void noMoreMatchesAvailable(LocalSearchMatcher matcher) {
        if (isDisposed) {
            return;
        }
        LocalSearchMatcher removedMatcher = runningMatchers.pop();
        if (runningMatchers.isEmpty()) {
            // After all the matching process finished set to halted in order to
            // be able to start a new debug session
            suspended = true;
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

        SearchPlan newPlan = newPlanOptional.orElse(null);
//		final List<SearchOperationViewerNode> viewNodes = createOperationsListFromExecutor(newPlanExecutor);
        //if (runningMatchers.size() == 1 && newPlan != null) {
            // Set the input when the top level matcher goes to the next plan
        //    PlatformUI.getWorkbench().getDisplay().syncExec(() -> localSearchDebugView.getOperationListViewer().setInput(viewModel));
        //} else 
        if(newPlan != null) {
            //viewModel.insertForCurrent(createOperationsListFromPlan(newPlan));
            setCurrentOperation(newPlan.getSourceBody());
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
    public void operationSelected(SearchPlan plan, ISearchOperation operation, MatchingFrame frame, boolean isBacktrack) {
        if (isDisposed) {
            return;
        }
        //viewModel.stepInto(plan, operation);
        setCurrentOperation(plan, operation, isBacktrack);

        checkForBreakPoint();
    }

    @Override
    public void operationExecuted(SearchPlan plan, ISearchOperation operation, MatchingFrame frame, boolean isSuccessful) {
        if (isDisposed) {
            return;
        }
        if (suspended) {
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

    public void continueMatching() {
        this.suspended = false;
    }
    
    public void suspendMatching() {
        this.suspended = true;
    }
    
    public boolean isPatternMatchingRunning() {
        return !hasFinished;
    }
    

    private void checkForBreakPoint() {
        if (localSearchDebugView != null && suspended) {
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
                if(shouldSelectOtherTab){
                    shouldSelectOtherTab = false;
                    localSearchDebugView.getMatchesTabFolder().setSelection(runningMatchers.size()-1);
                }
                //SearchOperationViewerNode lastSelected = viewModel.getLastSelected();
                localSearchDebugView.getOperationListViewer().collapseAll();
                //localSearchDebugView.getOperationListViewer().expandToLevel(lastSelected, 0);
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
    
    public void dispose() {
        isDisposed = true;
    }
}
