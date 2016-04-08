/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
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
public class RunLocalSearchHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            LocalSearchDebugView localSearchDebugView = (LocalSearchDebugView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LocalSearchDebugView.ID);
            if (localSearchDebugView != null && localSearchDebugView.getDebugger() != null) {
                localSearchDebugView.getDebugger().setHalted(false);
            }
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        }
        
        synchronized (LocalSearchDebugger.notifier) {
        	// notifies the thread that does the matching
            LocalSearchDebugger.notifier.notify();
        }
        
        return null;
    }

}
