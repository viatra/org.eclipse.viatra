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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.debug.model.breakpoint.RuleBreakpoint;
import org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser.AdaptableTransformationBrowser;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;

public class AddRuleBreakpointHandler extends AbstractHandler{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    
                    
                    AdaptableTransformationBrowser view = (AdaptableTransformationBrowser) PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage().showView(AdaptableTransformationBrowser.ID);
                    Object selection = view.getSelection();
                    if (selection instanceof AdaptableEVM) {
                        TransformationThread thread = TransformationThreadFactory.getInstance().getTransformationThread(((AdaptableEVM) selection).getIdentifier());
                        
                        //Open a dialog to define rule name
                        InputDialog dialog = new InputDialog(view.getSite().getShell() , "Create Rule Breakpoint", "Set Transformation Rule Id", "", null);
                        dialog.open();
                        String ruleId = dialog.getValue();
                        
                        if(thread!=null){
                            thread.addTransformationBreakpoint(new RuleBreakpoint(ruleId));
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
