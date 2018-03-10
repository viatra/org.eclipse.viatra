/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import java.util.Collection;
import java.util.List;

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
        final List<ISearchOperation> operationList = plan.getCompiledOperations();
        operationList.forEach(op -> createSearchOperationNode(node, op));
        createMatchFoundNode(node, plan);
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
    
    private IPlanNode createMatchFoundNode(IPlanNode parent, SearchPlanForBody plan) {
        final MatchFoundNode node = new MatchFoundNode(parent);
        parent.addChild(MatchFoundNode.ID, node);
        return node;
    }
    
    private void doCreateChildren(IPlanNode node, IPlanDescriptor desc) {
        final Collection<SearchPlanForBody> plans = desc.getPlan();
        plans.forEach(plan -> createBodyNode(node, plan));
    }
}
