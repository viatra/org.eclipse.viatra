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
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;

/**
 * Model type for the tree viewer in the local search debugger tooling
 * 
 * @author Marton Bur
 *
 */
public class SearchPlanViewModel {

	private List<SearchOperationViewerNode> topLevelElements;
	private LocalSearchDebugger debugger;
	
	private SearchOperationViewerNode lastSelected;
	
	public SearchOperationViewerNode getLastSelected(){
		return lastSelected;
	}
	
	public SearchPlanViewModel(List<SearchOperationViewerNode> topLevelElements){
		this.topLevelElements = topLevelElements;
	}

	public List<SearchOperationViewerNode> getTopLevelElements() {
		return topLevelElements;
	}
	
	public void setDebugger(LocalSearchDebugger localSearchDebugger) {
		this.debugger = localSearchDebugger;
	}
	
	public void insertForCurrent(List<SearchOperationViewerNode> viewNodeList) {
		boolean notInserted = true;
		while(notInserted){
			if(lastSelected.isMatcherBased()){
				lastSelected.setChildren(viewNodeList);
				notInserted = false;
			} else {
				lastSelected = lastSelected.getParent();
			}
		}
	}


	public void stepInto(){
		// Step the execution exactly one step forward
		doStepInto(topLevelElements);
	}

	private void doStepInto(List<SearchOperationViewerNode> sameLevelElements) {
		if(sameLevelElements .size() == 0){
			return;
		}
		SearchPlanExecutor planExecutor = sameLevelElements.get(0).getPlanExecutor();
		int currentOperation = planExecutor.getCurrentOperation();
		
		for (int i = 0; i < sameLevelElements.size(); i++) {
			SearchOperationViewerNode currentNode = sameLevelElements.get(i);
			if(i < currentOperation){
				currentNode.setOperationStatus(OperationStatus.EXECUTED);
			} else if (i == currentOperation){
				if(becameCurrent(currentNode)){
					// This is selected for execution now, so that check for breakpoint
					if(currentNode.isBreakpoint()){
						debugger.setHalted(true);
					}
					currentNode.setOperationStatus(OperationStatus.CURRENT);
					lastSelected = currentNode;
				}
				doStepInto(currentNode.getChildren());
			} else /*if (i > currentOperation)*/ {
				currentNode.setOperationStatus(OperationStatus.QUEUED);				
			}
		}
	}

	private boolean becameCurrent(SearchOperationViewerNode currentNode) {
		boolean fromQueued = currentNode.getOperationStatus().equals(OperationStatus.QUEUED);
		boolean fromExecuted = currentNode.getOperationStatus().equals(OperationStatus.EXECUTED);
		return fromQueued || fromExecuted;
	}
	
	public void stepBack(){
		// Step the execution exactly one step backward
		doStepBack(topLevelElements);
	}

	private void doStepBack(List<SearchOperationViewerNode> sameLevelElements) {
		if(sameLevelElements.size() == 0){
			return;
		}
		SearchOperationViewerNode firstNode = sameLevelElements.get(0);
		SearchPlanExecutor planExecutor = firstNode.getPlanExecutor();
		int currentOperation = planExecutor.getCurrentOperation();

		for (int i = 0; i < sameLevelElements.size(); i++) {
			SearchOperationViewerNode currentNode = sameLevelElements.get(i);
			if(i < currentOperation){
				currentNode.setOperationStatus(OperationStatus.EXECUTED);
			} else if (i == currentOperation){
				doStepBack(currentNode.getChildren());
				if(currentNode.getOperationStatus().equals(OperationStatus.EXECUTED)){
					// This is selected for execution now (stepped backward in the plan), so that check for breakpoint
					if(currentNode.isBreakpoint()){
						debugger.setHalted(true);
					}
				}
				currentNode.setOperationStatus(OperationStatus.CURRENT);
				lastSelected = currentNode;
			} else /*if (i > currentOperation)*/ {
				currentNode.setOperationStatus(OperationStatus.QUEUED);				
			}
		}
	}
	
	public void stepOver() {
		// TODO
		throw new UnsupportedOperationException("This feature is a TODO");
	}




	
}
