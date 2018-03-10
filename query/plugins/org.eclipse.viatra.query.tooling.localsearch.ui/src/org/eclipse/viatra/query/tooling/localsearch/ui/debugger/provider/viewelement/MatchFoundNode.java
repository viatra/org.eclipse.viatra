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

/**
 * Represents a match found node in the user interface
 * 
 */
public class MatchFoundNode extends AbstractPlanNode {

    public static final String ID = "MATCH_FOUND_NODE";
    
    public MatchFoundNode(IPlanNode parent) {
        super(parent);
    }

    @Override
    public String getLabelText() {
        return "Match found";
    }

}