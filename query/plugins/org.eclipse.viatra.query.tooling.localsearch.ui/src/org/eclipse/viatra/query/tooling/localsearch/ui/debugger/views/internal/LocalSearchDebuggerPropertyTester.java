/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi and IncQueryLabs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;

public class LocalSearchDebuggerPropertyTester extends PropertyTester {

    private static final String IS_DEBUGGER_RUNNING = "operational";
    /**
     * @since 1.4
     */
    public static final String DEBUGGER_RUNNING = "org.eclipse.viatra.query.tooling.localsearch.ui.debugger.operational";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (IS_DEBUGGER_RUNNING.equals(property) && receiver instanceof LocalSearchDebugView) {
            LocalSearchDebugView debugView = (LocalSearchDebugView) receiver;
            return debugView.getDebugger() != null && debugView.getDebugger().isPatternMatchingRunning();
        }
        return false;
    }

}
