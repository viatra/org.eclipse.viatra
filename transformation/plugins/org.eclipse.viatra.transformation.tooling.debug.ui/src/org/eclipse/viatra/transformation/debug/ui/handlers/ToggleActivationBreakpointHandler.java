/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ActivationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.util.ViatraDebuggerUtil;

public class ToggleActivationBreakpointHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
            if (selection instanceof IStructuredSelection
                    && ((IStructuredSelection) selection).getFirstElement() instanceof RuleActivation) {
                RuleActivation act = (RuleActivation)((IStructuredSelection) selection).getFirstElement(); 
                TransformationThread thread = ViatraDebuggerUtil.getThread(act.getTransformationState());
                if (thread != null) {
                    ActivationBreakpoint transformationBreakpoint = new ActivationBreakpoint(
                            ((RuleActivation) ((IStructuredSelection) selection).getFirstElement()).getTrace());
                    transformationBreakpoint.setMarker(thread.getTransformationType().getResource()
                            .createMarker(transformationBreakpoint.getMarkerIdentifier()));
                    transformationBreakpoint.setEnabled(true);

                    ITransformationBreakpoint breakpointToRemove = null;
                    for (IBreakpoint iBreakpoint : thread.getBreakpoints()) {
                        if (iBreakpoint.equals(transformationBreakpoint)) {
                            breakpointToRemove = transformationBreakpoint;
                        }
                    }
                    if (breakpointToRemove != null) {
                        DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(breakpointToRemove, true);
                    } else {
                        DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(transformationBreakpoint);
                    }
                }
            }

        } catch (CoreException e) {
            throw new ExecutionException("Error while toggling breakpoint", e);
        }
        return null;
    }

}
