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

import java.util.List;

public interface IPlanNode {

    OperationStatus getOperationStatus();
    void setOperationStatus(OperationStatus status);
    
    default void setOperationStatusTransitively(OperationStatus status) {
        setOperationStatus(status);
        getChildren().forEach(child ->child.setOperationStatusTransitively(status));
    }

    boolean isBreakpointSet();

    void toggleBreakpoint(boolean breakpoint);

    /**
     * Returns an unmodifiable list of children
     */
    List<IPlanNode> getChildren();
    
    void addChild(Object key, IPlanNode child);
    IPlanNode getChildByKey(Object key);

    IPlanNode getParent();

    String getLabelText();
    
    default boolean isMatcherCall() {
        return false;
    }

    default boolean skipPresentation() {
        return false;
    }
}