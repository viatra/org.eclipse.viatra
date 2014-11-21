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
package org.eclipse.incquery.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.SearchPlanView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Marton Bur
 *
 */
public class RunLocalSearchHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // step the matching process here
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchPlanView searchPlanView = (SearchPlanView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SearchPlanView.ID);
                    searchPlanView.setHalted(false);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });
        
        synchronized (LocalSearchDebugger.notifier) {
            LocalSearchDebugger.notifier.notify();
        }
        
        return null;
    }

}
