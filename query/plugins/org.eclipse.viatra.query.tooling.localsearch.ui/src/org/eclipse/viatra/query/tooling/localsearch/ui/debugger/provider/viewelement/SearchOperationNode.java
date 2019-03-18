/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.operations.ExtendOperationExecutor;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.CountOperation;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * Represents a search operation as a nodes in the search plan view
 * 
 * @author Marton Bur
 *
 */
public class SearchOperationNode extends AbstractPlanNode {

    private final String labelText;
    private final ISearchOperation searchOperation;
    private final boolean matcherBased;
    private final OperationKind operationKind;
    private final Map<Integer, String> variableNameMapping;

    public SearchOperationNode(PatternBodyNode parent, ISearchOperation searchOperation) {
        super(parent);
        this.variableNameMapping = parent.getVariableNameMapping();
        Preconditions.checkArgument(searchOperation != null);
        this.searchOperation = searchOperation;
        if (searchOperation instanceof ExtendOperationExecutor) {
            operationKind = OperationKind.EXTEND;
        } else if (searchOperation instanceof NACOperation) {
            operationKind = OperationKind.NAC;
        } else if (searchOperation instanceof CountOperation) {
            operationKind = OperationKind.COUNT;
        } else {
            // This case there is a check operation
            operationKind = OperationKind.CHECK;
        }
        
        matcherBased = searchOperation instanceof IPatternMatcherOperation;

        labelText = doCalculateLabel();
    }
    
    private String doCalculateLabel() {
        try {
            return searchOperation.toString(variableNameMapping::get);
        } catch (Exception e) {
            return "Error while calculating label: " + e.getMessage() + "(" + e.getClass().getSimpleName() + ")";
        }
    }

    public OperationKind getOperationKind() {
        return operationKind;
    }
    
    @Override
    public String getLabelText() {
        return labelText;
    }

    public ISearchOperation getSearchOperation() {
        return searchOperation;
    }

    @Override
    public boolean isMatcherCall() {
        return matcherBased;
    }
}
