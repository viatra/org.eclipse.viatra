/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanDescriptor;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanForBody;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;

public class ViewModelFactory {

    private final LocalSearchDebugger debugger;

    public ViewModelFactory(LocalSearchDebugger debugger) {
        this.debugger = debugger;
    }
    
    public IPlanNode createViewModel(IPlanDescriptor descriptor) {
        return createRootNode(descriptor);
    }
    
    private IPlanNode createRootNode(IPlanDescriptor descriptor) {
        DebugSessionNode root = new DebugSessionNode(descriptor.getQuery());
        doCreateChildren(root, descriptor);
        return root;
    }
    
    private IPlanNode createBodyNode(IPlanNode parent, SearchPlanForBody plan) {
        PatternBodyNode node = new PatternBodyNode(parent, plan);
        parent.addChild(plan.getBody(), node);
        plan.getCompiledOperations().forEach(op -> createSearchOperationNode(node, op));
        return node;
    }
    
    private IPlanNode createSearchOperationNode(PatternBodyNode parent, ISearchOperation operation) {
        SearchOperationNode node = new SearchOperationNode(parent, operation);
        parent.addChild(operation, node);
        if (operation instanceof IPatternMatcherOperation) {
            final MatcherReference reference = ((IPatternMatcherOperation)operation).getCallInformation().getReference();
            debugger.getSearchPlan(reference.getQuery(), reference.getAdornment())
                    .ifPresent(desc -> doCreateChildren(node, desc));
        }
        return node;
    }
    
    private void doCreateChildren(IPlanNode node, IPlanDescriptor desc) {
        desc.getPlan().forEach(plan -> createBodyNode(node, plan));
    }
}
