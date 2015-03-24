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

import java.util.List;
import java.util.Stack;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.operations.IMatcherBasedOperation;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.viewelement.SearchPlanViewModel;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.internal.BreakPointListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

/**
 * An adapter implementation for local search matchers to support debugging
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugger implements ILocalSearchAdapter {

	public static volatile Object notifier = new Object();
	private LocalSearchDebugView localSearchDebugView;
	private Stack<LocalSearchMatcher> runningMatchers;
	private Stack<SearchPlanExecutor> runningExecutors;
	private boolean startHandlerCalled = false;

    private boolean halted = true;
	private SearchPlanViewModel viewModel;

    
	public LocalSearchDebugView getLocalSearchDebugView() {
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
		// If a new debug session is starting, obtain the view
		if (startHandlerCalled) {
			startHandlerCalled = false;
			// Syncexec is assumed to be needed because of the showView call
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
						BreakPointListener breakPointListener = new BreakPointListener(LocalSearchDebugger.this);
						TreeViewer operationListViewer = localSearchDebugView.getOperationListViewer();
						operationListViewer.addDoubleClickListener(breakPointListener);
						localSearchDebugView.setDebugger(LocalSearchDebugger.this);

						runningMatchers = new Stack<LocalSearchMatcher>();
						runningExecutors = new Stack<SearchPlanExecutor>();

						// Init treeviewer related fields
						// TODO make sure that the initialization is done for every part
						// TODO
						// operationListContentProvider.getMatcherCurrentExecutorMappings().clear();
						localSearchDebugView.refreshView();
					} catch (PartInitException e) {
						// TODO proper logging
						e.printStackTrace();
					}
				}
			});
		}
		runningMatchers.push(lsMatcher);
	}

	@Override
	public void patternMatchingFinished(LocalSearchMatcher matcher) {
		runningMatchers.pop();
		if (runningMatchers.size() == 0) {
			// After all the matching process finished set to halted in order to
			// be able to start a new debug session
			halted = true;
			localSearchDebugView.refreshView();
		}
	}

	@Override
	public void planChanged(SearchPlanExecutor oldPlanExecutor, final SearchPlanExecutor newPlanExecutor) {

		if (oldPlanExecutor != null) {
			runningExecutors.pop();
		}
		if (newPlanExecutor != null) {
			runningExecutors.push(newPlanExecutor);
		}


//		final List<SearchOperationViewerNode> viewNodes = createOperationsListFromExecutor(newPlanExecutor);
		if (runningMatchers.size() == 1) {
			this.viewModel = new SearchPlanViewModel(createOperationsListFromExecutor(newPlanExecutor));
			this.viewModel.setDebugger(this);
			// Set the input when the top level matcher goes to the next plan
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					localSearchDebugView.getOperationListViewer().setInput(viewModel);
				}
			});
		} else if(newPlanExecutor != null) {
			viewModel.insertForCurrent(createOperationsListFromExecutor(newPlanExecutor));
		}

		// Manage tabs for matching frames
		PQuery querySpecification = runningMatchers.peek().getQuerySpecification();
		final int keySize = querySpecification.getParameters().size();
		String queryName = getSimpleQueryName(querySpecification);
		final TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(queryName);
		@SuppressWarnings("unchecked")
		List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
		if (!storedFrames.isEmpty()) {
			storedFrames.remove(storedFrames.size() - 1);
		}
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				BiMap<Integer, PVariable> variableMapping = newPlanExecutor.getVariableMapping();
				List<String> columnNames = Lists.newArrayList();
				for (int i = 0; i < variableMapping.size(); i++) {
					columnNames.add(variableMapping.get(i).getName());
				}
				localSearchDebugView.recreateColumns(columnNames, keySize, matchesViewer);
			}
		});
	}


	@Override
	public void executorInitializing(SearchPlanExecutor searchPlanExecutor, MatchingFrame frame) {
		// Add the new frame here to the list of frames. Its contents will change as matching advances
		String simpleQueryName = getSimpleQueryName(runningMatchers.peek().getQuerySpecification());
		TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(simpleQueryName);
		@SuppressWarnings("unchecked")
		List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
		if (!storedFrames.contains(frame)) {
			storedFrames.add(frame);
		}
	}

	@Override
	public void operationSelected(final SearchPlanExecutor planExecutor, final MatchingFrame frame) {

		viewModel.stepInto();

		LocalSearchMatcher matcher = null;
		try {
			matcher = getMatcherIfExists(planExecutor, frame);
		} catch (LocalSearchException e1) {
			// TODO proper logging
			e1.printStackTrace();
		}
		if (matcher != null) {
			List<SearchOperationViewerNode> viewNodeList = createOperationsListFromExecutor(matcher.getPlan().get(0));
			viewModel.insertForCurrent(viewNodeList);
		}

		checkForBreakPoint();
	}

	@Override
	public void operationExecuted(SearchPlanExecutor planExecutor, MatchingFrame frame) {
//		viewModel.stepBack();
		if (halted) {
			localSearchDebugView.refreshView();
		}
	}

	@Override
	public void matchFound(SearchPlanExecutor planExecutor, MatchingFrame frame) {
		MatchingFrame frameToStore = frame.clone();
		String simpleQueryName = getSimpleQueryName(runningMatchers.peek().getQuerySpecification());
		TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(simpleQueryName);
		@SuppressWarnings("unchecked")
		List<MatchingFrame> storedFrames = (List<MatchingFrame>) matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
		storedFrames.add(storedFrames.size() - 1, frameToStore);
	}

	public void setHalted(boolean halted) {
		this.halted = halted;
	}

	// TODO might not be needed by others
	public boolean isHalted() {
		return halted;
	}
	
	
	private List<SearchOperationViewerNode> createOperationsListFromExecutor(SearchPlanExecutor planExecutor) {
		List<SearchOperationViewerNode> nodes = Lists.newArrayList();
		
		List<ISearchOperation> plan = ((SearchPlanExecutor)planExecutor).getSearchPlan().getOperations();
		for (ISearchOperation operation : plan) {
			nodes.add(new SearchOperationViewerNode(operation, planExecutor));
		}
		// Final "match found" indicator operation
		nodes.add(new SearchOperationViewerNode(planExecutor));
		
		return nodes;
	}

	private LocalSearchMatcher getMatcherIfExists(SearchPlanExecutor planExecutor, MatchingFrame frame)
			throws LocalSearchException {

		int currentOperationIndex = planExecutor.getCurrentOperation();
		if (currentOperationIndex <= 0 || currentOperationIndex >= planExecutor.getSearchPlan().getOperations().size()) {
			return null;
		}
		ISearchOperation currentOperation = planExecutor.getSearchPlan().getOperations().get(currentOperationIndex);

		LocalSearchMatcher calledMatcher = null;
		if (currentOperation instanceof IMatcherBasedOperation) {
			calledMatcher = ((IMatcherBasedOperation) currentOperation).getAndPrepareCalledMatcher(frame,planExecutor.getContext());
		}
		return calledMatcher;
	}

	private String getSimpleQueryName(PQuery query) {
		String[] stringTokens = query.getFullyQualifiedName().split("\\.");
		String queryName = stringTokens[stringTokens.length - 1];
		return queryName;
	}

	private void checkForBreakPoint() {
		if (localSearchDebugView != null) {
			if (halted) {
				localSearchDebugView.refreshView();
				synchronized (notifier) {
					try {
						notifier.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}
}
