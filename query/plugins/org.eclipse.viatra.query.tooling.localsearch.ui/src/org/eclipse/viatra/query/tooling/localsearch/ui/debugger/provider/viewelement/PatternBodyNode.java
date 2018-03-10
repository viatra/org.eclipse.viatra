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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;

/**
 * Represents a pattern body in the user interface
 * 
 */
public class PatternBodyNode extends AbstractPlanNode {

    private final Map<Integer, String> variableNameMapping;
    private final SearchPlanForBody plan;

    public PatternBodyNode(IPlanNode parent, SearchPlanForBody plan) {
        super(parent);
        this.plan = plan;
        variableNameMapping = Collections.unmodifiableMap(plan.getVariableKeys().entrySet().stream()
                .collect(Collectors.toMap(Entry::getValue, e -> e.getKey().getName())));
    }

    public PBody getRelatedBody() {
        return plan.getBody();
    }
    
    @Override
    public String getLabelText() {
        return "Pattern Body";
    }

    @Override
    public boolean skipPresentation() {
        return parent.getChildren().size() == 1;
    }
    
    public Map<Integer, String> getVariableNameMapping() {
        return variableNameMapping;
    }
}