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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.IPlanNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.OperationStatus;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.PatternBodyNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.ViewModelFactory;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal.LocalSearchDebuggerPropertyTester;

/**
 * An adapter implementation for local search matchers to support debugging
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugger implements ILocalSearchAdapter {

    public volatile Object notifier = new Object();
    private final LocalSearchDebugView localSearchDebugView;
    private volatile boolean isDisposed = false;
    private boolean hasFinished = false;
    private Deque<IPlanNode> currentOperation;

    private boolean suspended = true;

    private final AdvancedViatraQueryEngine queryEngine;
    private final IQuerySpecification<?> rootSpecification;
    private final Object[] adornment;
    private final ViewModelFactory factory = new ViewModelFactory(this);
    
    private final IPlanNode viewModel;
    
    
    public LocalSearchDebugger(LocalSearchDebugView localSearchDebugView, AdvancedViatraQueryEngine queryEngine,
            IQuerySpecification<?> rootSpecification, Object[] adornment) {
        this.localSearchDebugView = localSearchDebugView;
        this.queryEngine = queryEngine;
        this.rootSpecification = rootSpecification;
        this.adornment = adornment;
        this.viewModel = getSearchPlan().map(factory::createViewModel).orElse(null);
        
        this.currentOperation = new ArrayDeque<>();
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
    }

    private void setCurrentOperation(PBody body) {
        IPlanNode childNode = null;
        if (currentOperation.isEmpty()) {
            // Root pattern
            childNode = viewModel.getChildByKey(body);
        } else {
            // Called pattern
            final IPlanNode current = currentOperation.peek();
            if (current instanceof SearchOperationNode) {
                if (current.isMatcherCall() && current.getChildByKey(body) != null) {
                    // In case of pattern calls, check child nodes
                    childNode = current.getChildByKey(body);
                }
            } if (current instanceof PatternBodyNode && Objects.equals(((PatternBodyNode) current).getRelatedBody().getPattern(), body.getPattern())) {
                current.setOperationStatus(OperationStatus.EXECUTED);
                currentOperation.pop();
                childNode = currentOperation.peek().getChildByKey(body);
            }
        }
        currentOperation.push(Objects.requireNonNull(childNode, "Child node not found"));
        childNode.setOperationStatus(OperationStatus.CURRENT);
    }
    
    private void setCurrentOperation(SearchPlan plan, ISearchOperation operation, boolean isBacktrack) {
        IPlanNode node = currentOperation.peek();
        if (node instanceof SearchOperationNode) {
            if (Objects.equals(((PatternBodyNode)node.getParent()).getRelatedBody(), plan.getSourceBody())) {
                currentOperation.pop(); // Remove previous operation
                final IPlanNode currentParent = currentOperation.peek();
                currentParent.setOperationStatus(OperationStatus.CURRENT);
                final IPlanNode childNode = currentParent.getChildByKey(operation);
                currentOperation.push(childNode);
            }
        } else if (node instanceof PatternBodyNode){
            if (Objects.equals(((PatternBodyNode)node).getRelatedBody(), plan.getSourceBody())) {
                // The new operation is still in the current body
                final IPlanNode childNode = node.getChildByKey(operation);
                currentOperation.push(childNode);
            }
        }
    }
    
    @Override
    public void noMoreMatchesAvailable(LocalSearchMatcher matcher) {
        if (isDisposed) {
            return;
        }
        if (currentOperation.isEmpty()) {
            // After all the matching process finished set to halted in order to
            // be able to start a new debug session
            suspended = true;
            hasFinished = true;
            localSearchDebugView.refreshView(rootSpecification.getInternalQueryRepresentation(), null);
        } else {
            PatternBodyNode node = (PatternBodyNode) currentOperation.pop();
            node.setOperationStatus(OperationStatus.EXECUTED);
            waitForMatchingToContinue();
        }
    }

    @Override
    public void planChanged(Optional<SearchPlan> oldPlanOptional, Optional<SearchPlan> newPlanOptional) {
        if (isDisposed) {
            return;
        }

        newPlanOptional.ifPresent(newPlan -> {
            setCurrentOperation(newPlan.getSourceBody());

            // Manage tabs for matching frames
            PQuery querySpecification = newPlan.getSourceBody().getPattern();
            final int keySize = querySpecification.getParameters().size();
            final TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(querySpecification);
            @SuppressWarnings("unchecked")
            List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
            if (!storedFrames.isEmpty()) {
                storedFrames.remove(storedFrames.size() - 1);
            }
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
                Map<Integer, PVariable> variableMapping = newPlan.getVariableMapping();
                List<String> columnNames = new ArrayList<>();
                for (int i = 0; i < variableMapping.size(); i++) {
                    columnNames.add(variableMapping.get(i).getName());
                }
                localSearchDebugView.recreateColumns(columnNames, keySize, matchesViewer);
            });
        });
    }


    @Override
    public void executorInitializing(SearchPlan searchPlan, MatchingFrame frame) {
        if (isDisposed) {
            return;
        }
        // Add the new frame here to the list of frames. Its contents will change as matching advances
        TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(searchPlan.getSourceBody().getPattern());
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
        setCurrentOperation(plan, operation, isBacktrack);
        final IPlanNode node = currentOperation.peek();
        if (isBacktrack) {
            final PatternBodyNode bodyNode = getCurrentlyExecutedBodyNode();
            bodyNode.getChildren().stream()
                .skip(plan.getOperationIndex(operation))
                .forEach(n -> n.setOperationStatus(OperationStatus.QUEUED));
        }
        node.setOperationStatus(OperationStatus.CURRENT);
        waitForMatchingToContinue();
    }

    @Override
    public void operationExecuted(SearchPlan plan, ISearchOperation operation, MatchingFrame frame, boolean isSuccessful) {
        if (isDisposed) {
            return;
        }
        final IPlanNode node = currentOperation.pop();
        Preconditions.checkState(node instanceof SearchOperationNode && Objects.equals(operation, ((SearchOperationNode)node).getSearchOperation()));
        if (isSuccessful) {
            node.setOperationStatus(OperationStatus.EXECUTED);
        } else {
            node.setOperationStatusTransitively(OperationStatus.QUEUED);
        }
    }

    @Override
    public void matchFound(SearchPlan plan, MatchingFrame frame) {
        if (isDisposed) {
            return;
        }
        MatchingFrame frameToStore = new MatchingFrame(frame);
        TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(plan.getSourceBody().getPattern());
        
        
        @SuppressWarnings("unchecked")
        List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
        storedFrames.add(storedFrames.size() - 1, frameToStore);
        
        PatternBodyNode node = getCurrentlyExecutedBodyNode();
        node.setOperationStatus(OperationStatus.CURRENT);
        waitForMatchingToContinue();
        node.setOperationStatus(OperationStatus.EXECUTED);
    }

    public void continueMatching() {
        this.suspended = false;
    }
    
    private void waitForMatchingToContinue() {
        if (localSearchDebugView != null) {
            if (!currentOperation.isEmpty() && currentOperation.peek().isBreakpointSet()) {
                suspended = true;
            }
            if (suspended) {
                // Ensure user interface is up-to-date and select current context
                localSearchDebugView.refreshView(getCurrentlyExecutedQuery(), currentOperation.peek());
                
                synchronized (notifier) {
                    try {
                        notifier.wait();
                    } catch (InterruptedException e) {
                        dispose();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
    
    public boolean isPatternMatchingRunning() {
        return !hasFinished;
    }
    
    private PatternBodyNode getCurrentlyExecutedBodyNode() {
        IPlanNode node = currentOperation.peek();
        while (node != null && !(node instanceof PatternBodyNode)) {
            node = node.getParent();
        }
        return (PatternBodyNode)node;
    }
    
    private PQuery getCurrentlyExecutedQuery() {
        PatternBodyNode node = getCurrentlyExecutedBodyNode();
        if (node != null) {
            return node.getRelatedBody().getPattern();
        } else {
            return rootSpecification.getInternalQueryRepresentation();
        }
    }
    
    public void dispose() {
        isDisposed = true;
    }
}
