/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IViewPart;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;

public class LocalSearchDebuggerPropertyTester extends PropertyTester {

    private final String IS_DEBUGGER_RUNNING = "operational";
    /**
     * @since 1.4
     */
    public static final String DEBUGGER_RUNNING = "org.eclipse.viatra.query.tooling.localsearch.ui.debugger.operational";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (IS_DEBUGGER_RUNNING.equals(property) && receiver instanceof IViewPart) {
            if (receiver instanceof LocalSearchDebugView) {
                LocalSearchDebugView debugView = (LocalSearchDebugView) receiver;
                return debugView.getDebugger() != null;
            }
        }
        return false;
    }

}
