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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.ui.actions.ExportBreakpointsOperation;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.debug.model.breakpoint.RuleBreakpoint;
import org.eclipse.viatra.transformation.debug.ui.util.BreakpointCacheUtil;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;

public class AddRuleBreakpointHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
            if (selection instanceof IStructuredSelection
                    && ((IStructuredSelection) selection).getFirstElement() instanceof AdaptableEVM) {
                TransformationThread thread = TransformationThreadFactory.getInstance().getTransformationThread(
                        ((AdaptableEVM) ((IStructuredSelection) selection).getFirstElement()).getIdentifier());

                // Open a dialog to define rule name
                InputDialog dialog = new InputDialog(HandlerUtil.getActiveShellChecked(event), "Create Rule Breakpoint",
                        "Set Transformation Rule Id", "", null);
                dialog.open();
                String ruleId = dialog.getValue();

                if (thread != null && dialog.getReturnCode() == Window.OK) {
                    RuleBreakpoint breakpoint = new RuleBreakpoint(ruleId);
                    breakpoint.setMarker(thread.getTransformationType().getResource()
                            .createMarker(breakpoint.getMarkerIdentifier()));
                    breakpoint.setEnabled(true);
                    DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
                    ExportBreakpointsOperation operation = new ExportBreakpointsOperation(
                            BreakpointCacheUtil.filterBreakpoints(thread.getBreakpoints()),
                            BreakpointCacheUtil.getBreakpointCacheLocation());
                    operation.run(null);
                }
            }

        } catch (CoreException | InvocationTargetException e) {
            throw new ExecutionException("Error while adding rule breakpoint", e);
        }
        return null;
    }

}
