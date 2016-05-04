/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.debug.ui.views.ActivationBrowser;
import org.eclipse.viatra.transformation.evm.api.Activation;

public class AddBreakpointHandler extends AbstractHandler{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    ActivationBrowser view = (ActivationBrowser) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ActivationBrowser.ID);
                    Activation<?> selection = view.getSelection();
                    
                    //TODO Temporary
                    for(TransformationThread thread : TransformationThreadFactory.INSTANCE.getTransformationThreads()){
                        thread.toggleBreakPoint(selection);
                    }
                    
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }
}
