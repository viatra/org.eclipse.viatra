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

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.operations.check.BinaryTransitiveClosureCheck;
import org.eclipse.incquery.runtime.localsearch.operations.check.CountCheck;
import org.eclipse.incquery.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.incquery.runtime.localsearch.operations.extend.CountOperation;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
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
	private List<LocalSearchMatcher> runningMatchers;
	
	private boolean startHandlerCalled = false;
	
	public boolean isStartHandlerCalled() {
		return startHandlerCalled;
	}

	public void setStartHandlerCalled(boolean startHandlerCalled) {
		this.startHandlerCalled = startHandlerCalled;
	}

	private OperationListContentProvider operationListContentProvider;
	
	@Override
	public void patternMatchingStarted(final LocalSearchMatcher lsMatcher) {
		// If a new debug session is starting, obtain the view
		if(startHandlerCalled){
			startHandlerCalled = false;
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						runningMatchers = Lists.newArrayList();
						// Init treeviewer related fields
						localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
						operationListContentProvider = localSearchDebugView.getOperationListContentProvider();
						operationListContentProvider.getMatcherCurrentExecutorMappings().clear();
						localSearchDebugView.refreshOperationList();
						localSearchDebugView.refreshGraph();
						
					} catch (PartInitException e) {
						// TODO proper logging
						e.printStackTrace();
					}
				}
			});
		}
		runningMatchers.add(lsMatcher);
	}

	@Override
	public void patternMatchingFinished(LocalSearchMatcher matcher) {
		runningMatchers.remove(matcher);
		if(runningMatchers.size() == 0){
			// After all the matching process finished set to halted in order to 
			// be able to start a new debug session
			localSearchDebugView.setHalted(true);
			localSearchDebugView.refreshOperationList();
			localSearchDebugView.refreshGraph();
		}
	}

	@Override
	public void planStarted(final SearchPlanExecutor planExecutor) {
		if (runningMatchers.size() == 1) {
			// Set the input when the top level matcher goes to the next plan
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					localSearchDebugView.getOperationListViewer().setInput(planExecutor);
				}
			});
		}
	}

	@Override
	public void planFinished(SearchPlanExecutor planExecutor) {
		// Handle plan finished event here
	}

	@Override
	public void operationSelected(final SearchPlanExecutor planExecutor, final MatchingFrame frame) {
		
		LocalSearchMatcher matcher = getMatcherIfExists(planExecutor);
		if(matcher != null){
			runningMatchers.add(matcher);
		}
		
		// select corresponding matcher
		LocalSearchMatcher currentMatcher = null;
		for (LocalSearchMatcher localSearchMatcher : runningMatchers) {
			if(localSearchMatcher.getPlan().contains(planExecutor)){
				currentMatcher = localSearchMatcher;
			}
		}
		
		if(currentMatcher != null){
			operationListContentProvider.getMatcherCurrentExecutorMappings().put(currentMatcher, planExecutor) ;
		}
		
		if (localSearchDebugView.isHalted()) { 
			// TODO is this refresh here needed?
			localSearchDebugView.refreshOperationList();
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() { 
					localSearchDebugView.getOperationListLabelProvider().addPlanExecutor(planExecutor);					
					GraphViewer graphViewer = localSearchDebugView.getGraphViewer();

					TableViewer matchesViewer = localSearchDebugView.getMatchesViewer();

					BiMap<Integer,PVariable> variableMapping = planExecutor.getVariableMapping();
					List<String> columnNames = Lists.newArrayList();
					for (int i = 0; i < variableMapping.size(); i++ ) {
						columnNames.add(variableMapping.get(i).getName());
					}
					localSearchDebugView.recreateColumns(columnNames);

					// TODO here a new match should be registered instead of calling set input: the old frames will also be needed
					Object[] originalElements = frame.getElements();
					Object[] elements = Arrays.copyOf(originalElements,originalElements.length);
					for (int i = 0; i < elements.length; i++) {
						if(elements[i]==null){
							elements[i] = "";
						}
					}
					matchesViewer.setInput(new MatchingFrame[]{frame});
					matchesViewer.refresh();
					
					// TODO the graph viewer should show the frame selected in the TableViewer
					// Redraw the matching frame - stateless implementation
					graphViewer.setInput(frame);
					
				}
			});
		}
		
		if (localSearchDebugView != null) {
			if (localSearchDebugView.isBreakpointHit(planExecutor)) {
				synchronized (notifier) {
					try {
						PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								localSearchDebugView.getOperationListViewer().refresh();
								localSearchDebugView.getGraphViewer().refresh();
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

	private LocalSearchMatcher getMatcherIfExists(SearchPlanExecutor planExecutor) {
		int currentOperation = planExecutor.getCurrentOperation();
		if(currentOperation <= 0 || currentOperation >= planExecutor.getSearchPlan().getOperations().size()){
			return null;
		}
		ISearchOperation operation = planExecutor.getSearchPlan().getOperations().get(currentOperation);

		// TODO carry on with the debug phase until the plan is fully loaded for these special search operations
		
		LocalSearchMatcher calledMatcher = null;
		if (operation instanceof NACOperation) {
			calledMatcher = ((NACOperation) operation).getCalledMatcher();
		} else if (operation instanceof BinaryTransitiveClosureCheck) {
			calledMatcher = ((BinaryTransitiveClosureCheck) operation).getCalledMatcher();
		} else if (operation instanceof CountOperation) {
			calledMatcher = ((CountOperation) operation).getCalledMatcher();
		} else if (operation instanceof CountCheck) {
			calledMatcher = ((CountCheck) operation).getCalledMatcher();
		}
		return calledMatcher;
	}

	@Override
	public void operationExecuted(SearchPlanExecutor planExecutor, MatchingFrame frame) {
		// Handle operation executed event here
	}


}
