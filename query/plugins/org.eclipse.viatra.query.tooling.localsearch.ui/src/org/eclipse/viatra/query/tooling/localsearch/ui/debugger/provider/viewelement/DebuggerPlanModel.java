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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlan;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;

/**
 * Model type for the tree viewer in the local search debugger tooling
 * 
 * TODO this class should not be used anymore, but kept until the replacement is 100% functional
 * 
 * @author Marton Bur
 *
 */
public class DebuggerPlanModel {

    private List<AbstractPlanNode> topLevelElements;
    private LocalSearchDebugger debugger;
    
    private AbstractPlanNode lastSelected;
    
    public AbstractPlanNode getLastSelected(){
        return lastSelected;
    }
    
    public DebuggerPlanModel(SearchPlan plan) {
        topLevelElements = new ArrayList<>();
        
        for (ISearchOperation operation : plan.getOperations()) {
            //topLevelElements.add(new SearchOperationNode(operation, plan));
        }
        // Final "match found" indicator operation
        //topLevelElements.add(new MatchFoundNode(null, plan));
    }

    public List<AbstractPlanNode> getTopLevelElements() {
        return topLevelElements;
    }
    
    public void setDebugger(LocalSearchDebugger localSearchDebugger) {
        this.debugger = localSearchDebugger;
    }
    
    public void insertForCurrent(List<AbstractPlanNode> viewNodeList) {
        boolean notInserted = true;
        while(notInserted){
            if(lastSelected.isMatcherCall()){
                //lastSelected.setChildren(viewNodeList);
                notInserted = false;
            } else {
                //lastSelected = lastSelected.getParent();
            }
        }
    }


    public void stepInto(SearchPlan plan, ISearchOperation operation){
        // Step the execution exactly one step forward
        doStepInto(topLevelElements, plan, operation);
    }

    private void doStepInto(List<AbstractPlanNode> sameLevelElements, SearchPlan plan, ISearchOperation operation) {
        if (sameLevelElements.isEmpty()) {
            return;
        }
        int currentOperationIndex = plan.getOperationIndex(operation);
        
        for (int i = 0; i < sameLevelElements.size(); i++) {
            AbstractPlanNode currentNode = sameLevelElements.get(i);
            if(i < currentOperationIndex){
                currentNode.setOperationStatus(OperationStatus.EXECUTED);
            } else if (i == currentOperationIndex){
                if(becameCurrent(currentNode)){
                    // This is selected for execution now, so that check for breakpoint
                    if(currentNode.isBreakpointSet()){
                        //debugger.setHalted(true);
                    }
                    currentNode.setOperationStatus(OperationStatus.CURRENT);
                    lastSelected = currentNode;
                }
                //doStepInto(currentNode.getChildren(), plan, operation);
            } else /*if (i > currentOperation)*/ {
                currentNode.setOperationStatus(OperationStatus.QUEUED);				
            }
        }
    }

    private boolean becameCurrent(AbstractPlanNode currentNode) {
        boolean fromQueued = currentNode.getOperationStatus().equals(OperationStatus.QUEUED);
        boolean fromExecuted = currentNode.getOperationStatus().equals(OperationStatus.EXECUTED);
        return fromQueued || fromExecuted;
    }
    
}
