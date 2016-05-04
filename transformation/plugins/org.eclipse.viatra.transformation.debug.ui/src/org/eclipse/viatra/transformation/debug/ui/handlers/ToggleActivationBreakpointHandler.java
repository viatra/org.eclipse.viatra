/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.breakpoint.TransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.ui.util.DebugUIUtil;
import org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser.AdaptableTransformationBrowser;
import org.eclipse.viatra.transformation.evm.api.Activation;

public class ToggleActivationBreakpointHandler extends AbstractHandler{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    AdaptableTransformationBrowser view = (AdaptableTransformationBrowser) PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage().showView(AdaptableTransformationBrowser.ID);
                    Object selection = view.getSelection();
                    if (selection instanceof Activation<?>) {
                        TransformationThread thread = DebugUIUtil.getActivationThread((Activation<?>) selection);
                        if(thread!=null){
                            thread.toggleTransformationBreakPoint(new TransformationBreakpoint((Activation<?>) selection));
                        }
                    }

                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

}
