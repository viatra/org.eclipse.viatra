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
