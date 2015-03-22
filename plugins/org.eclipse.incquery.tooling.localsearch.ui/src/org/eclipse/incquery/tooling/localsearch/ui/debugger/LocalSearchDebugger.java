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
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.OperationListContentProvider;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

/**
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugger implements ILocalSearchAdapter {

	public static volatile Object notifier = new Object();
	private LocalSearchDebugView localSearchDebugView;
	private Stack<LocalSearchMatcher> runningMatchers;
	private OperationListContentProvider operationListContentProvider;
	private boolean startHandlerCalled = false;
	
	
	public boolean isStartHandlerCalled() {
		return startHandlerCalled;
	}

	public void setStartHandlerCalled(boolean startHandlerCalled) {
		this.startHandlerCalled = startHandlerCalled;
	}

	
	@Override
	public void patternMatchingStarted(final LocalSearchMatcher lsMatcher) {
		// If a new debug session is starting, obtain the view
		if(startHandlerCalled){
			startHandlerCalled = false;
			// Syncexec is assumed to be needed because of the showView call
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						runningMatchers = new Stack<LocalSearchMatcher>();
						// Init treeviewer related fields
						// TODO make sure that the initialization is done for every part
						localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
						localSearchDebugView.getBreakpoints().clear();
						operationListContentProvider = localSearchDebugView.getOperationListContentProvider();
						operationListContentProvider.getMatcherCurrentExecutorMappings().clear();
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
		if(runningMatchers.size() == 0){
			// After all the matching process finished set to halted in order to 
			// be able to start a new debug session
			localSearchDebugView.setHalted(true);
			localSearchDebugView.refreshView();
		}
	}


	@Override
	public void planChanged(SearchPlanExecutor oldPlanExecutor, final SearchPlanExecutor newPlanExecutor) {

		// Select corresponding matcher and store the (matcher,executor) pair
		// This mapping is needed for the providers for the locaSearchDebugView 
		// to get the information for matchers and their currently executed plans
		operationListContentProvider.getMatcherCurrentExecutorMappings().put(runningMatchers.peek(), newPlanExecutor);
		
		if (runningMatchers.size() == 1) {
			// Set the input when the top level matcher goes to the next plan
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					localSearchDebugView.getOperationListViewer().setInput(newPlanExecutor);
				}
			});
		}

		List<SearchPlanExecutor> planExecutorList = localSearchDebugView.getOperationListLabelProvider().getPlanExecutorList();
		planExecutorList.remove(oldPlanExecutor);
		planExecutorList.add(newPlanExecutor);
		
		PQuery querySpecification = runningMatchers.peek().getQuerySpecification();
		final int keySize = querySpecification.getParameters().size();
		String queryName = getSimpleQueryName(querySpecification);
		final TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(queryName);
		@SuppressWarnings("unchecked")
		List<MatchingFrame> storedFrames = (List<MatchingFrame>)matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
		if(!storedFrames.isEmpty()){
			storedFrames.remove(storedFrames.size()-1);
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
		List<MatchingFrame> storedFrames = (List<MatchingFrame>)matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
		if(!storedFrames.contains(frame)){
			storedFrames.add(frame);
		}

//		synchronized (notifier) {
//			try {
//				notifier.wait();
//			} catch (InterruptedException e) {
//				Thread.currentThread.interrupt();
//			}
//		}
		
	}
	
	@Override
	public void operationSelected(final SearchPlanExecutor planExecutor, final MatchingFrame frame) {

		int currentOperationIndex = planExecutor.getCurrentOperation();

		if (currentOperationIndex >= planExecutor.getSearchPlan().getOperations().size()) {
			// A match was found previously so that the index is greater than the index of the last operation, no operation left in the plan
			// It is still possible that the user placed a breakpoint on the match found proxy operation
			checkForBreakPoint(planExecutor);
			return;
		} else if (currentOperationIndex < 0){
			// The plan has been executed, index is out of range
			return;
		}
		
		// TODO the above line should be in planChanged
		LocalSearchMatcher matcher = null;
		try {
			matcher = getMatcherIfExists(planExecutor,frame);
		} catch (LocalSearchException e1) {
			// TODO proper logging
			e1.printStackTrace();
		}
		if (matcher != null) {
			operationListContentProvider.getMatcherCurrentExecutorMappings().put(matcher, matcher.getPlan().get(0));
		}

		checkForBreakPoint(planExecutor);
	}

	private void checkForBreakPoint(final SearchPlanExecutor planExecutor) {
		if (localSearchDebugView != null) {
			if (localSearchDebugView.isBreakpointHit(planExecutor)) {
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

	@Override
	public void operationExecuted(SearchPlanExecutor planExecutor, MatchingFrame frame) {
		// Handle operation executed event here -- TODO the next operation might be selected here, so check for halting condition
		if(localSearchDebugView.isHalted()){
			localSearchDebugView.refreshView();
		}
	}

	@Override
	public void matchFound(SearchPlanExecutor planExecutor, MatchingFrame frame) {
		MatchingFrame frameToStore = frame.clone();
		String simpleQueryName = getSimpleQueryName(runningMatchers.peek().getQuerySpecification());
		TableViewer matchesViewer = localSearchDebugView.getMatchesViewer(simpleQueryName);
		@SuppressWarnings("unchecked")
		List<MatchingFrame> storedFrames = (List<MatchingFrame>)matchesViewer.getData(LocalSearchDebugView.VIEWER_KEY);
		storedFrames.add(storedFrames.size()-1, frameToStore);
	}

	private LocalSearchMatcher getMatcherIfExists(SearchPlanExecutor planExecutor, MatchingFrame frame) throws LocalSearchException {
		
		int currentOperationIndex = planExecutor.getCurrentOperation();
		if(currentOperationIndex <= 0 || currentOperationIndex >= planExecutor.getSearchPlan().getOperations().size()){
			return null;
		}
		ISearchOperation currentOperation = planExecutor.getSearchPlan().getOperations().get(currentOperationIndex);

		// TODO carry on with the debug phase until the plan is fully loaded for these special search operations
		// TODO  /|\ this is being elaborated now... this might not be an option for now
		
		LocalSearchMatcher calledMatcher = null;
		if (currentOperation instanceof IMatcherBasedOperation) {
			calledMatcher = ((IMatcherBasedOperation) currentOperation).getAndPrepareCalledMatcher(frame, planExecutor.getContext());
		}
		return calledMatcher;
	}
	
	private String getSimpleQueryName(PQuery query) {
		String[] stringTokens = query.getFullyQualifiedName().split("\\.");
		String queryName = stringTokens[stringTokens.length-1];
		return queryName;
	}


}
