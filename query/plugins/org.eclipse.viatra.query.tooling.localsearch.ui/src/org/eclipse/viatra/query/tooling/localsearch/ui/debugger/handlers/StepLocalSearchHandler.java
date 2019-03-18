/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;

/**
 * 
 * @author Marton Bur
 *
 */
public class StepLocalSearchHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            LocalSearchDebugView localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
            final LocalSearchDebugger debugger = localSearchDebugView.getDebugger();
            if (debugger != null) {
                synchronized (debugger.notifier) {
                    // notifies the thread that does the matching
                    debugger.notifier.notify();
                }
            }
        } catch (PartInitException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
        return null;
    }

}
