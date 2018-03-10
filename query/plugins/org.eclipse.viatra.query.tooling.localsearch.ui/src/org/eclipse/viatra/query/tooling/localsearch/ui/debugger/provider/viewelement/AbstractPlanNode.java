/*******************************************************************************
 * Copyright (c) 2010-2018, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro and IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *   Zoltan Ujhelyi - restructuring of debugger
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This viewer node manages the parent-child relationships and general execution and breakpoint state for the plan
 * execution.
 *
 */
public abstract class AbstractPlanNode implements IPlanNode {
    final List<IPlanNode> children;
    final Map<Object, IPlanNode> indexedChildren;
    final IPlanNode parent;
    
    private OperationStatus operationStatus;
    private boolean breakpointSet;
    
    public AbstractPlanNode(IPlanNode parent) {
        super();
        children = new ArrayList<>();
        indexedChildren = new HashMap<>();
        
        this.parent = parent;
        
        operationStatus = OperationStatus.QUEUED;
        breakpointSet = false;
    }

    @Override
    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    @Override
    public void setOperationStatus(OperationStatus operationStatus) {
        if (operationStatus == OperationStatus.EXECUTED) {
            children.forEach(child -> child.setOperationStatus(OperationStatus.EXECUTED));
        }
        this.operationStatus = operationStatus;
    }

    @Override
    public boolean isBreakpointSet() {
        return breakpointSet;
    }

    @Override
    public void toggleBreakpoint(boolean breakpointSet) {
        this.breakpointSet = breakpointSet;
    }

    @Override
    public List<IPlanNode> getChildren() {
        return children;
    }

    @Override
    public IPlanNode getParent() {
        return parent;
    }
    
    @Override
    public void addChild(Object key, IPlanNode child) {
        children.add(child);
        indexedChildren.put(key, child);
    }
    
    @Override
    public IPlanNode getChildByKey(Object key) {
        return indexedChildren.get(key);
    }
}