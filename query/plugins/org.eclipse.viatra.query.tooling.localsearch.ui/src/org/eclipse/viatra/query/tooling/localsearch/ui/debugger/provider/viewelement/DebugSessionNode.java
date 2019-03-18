/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

public class DebugSessionNode extends AbstractPlanNode {

    private final PQuery query;

    public DebugSessionNode(PQuery query) {
        super(null);
        this.query = query;
    }

    @Override
    public IPlanNode getParent() {
        return null;
    }

    @Override
    public String getLabelText() {
        return query.getFullyQualifiedName();
    }

}
