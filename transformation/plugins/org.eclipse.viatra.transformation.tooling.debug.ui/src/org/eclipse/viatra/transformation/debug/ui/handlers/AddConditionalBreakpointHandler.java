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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ConditionalTransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.util.ViatraDebuggerUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class AddConditionalBreakpointHandler extends AbstractHandler {
    @Inject
    private Injector injector;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
            if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).getFirstElement() instanceof TransformationState) {
                TransformationState state = (TransformationState)((IStructuredSelection) selection).getFirstElement(); 
                TransformationThread thread = ViatraDebuggerUtil.getThread(state);

                // Open a dialog to define conditions
                ConditionalBreakpointDialog dialog = new ConditionalBreakpointDialog(
                        HandlerUtil.getActiveShellChecked(event), injector);
                dialog.create();
                dialog.open();
                if (dialog.getReturnCode() == Window.OK && thread != null) {
                    String results = dialog.getResults();
                    ConditionalTransformationBreakpoint breakpoint = new ConditionalTransformationBreakpoint(results);
                    breakpoint.setMarker(thread.getTransformationType().getResource()
                            .createMarker(breakpoint.getMarkerIdentifier()));
                    breakpoint.setEnabled(true);
                    DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
                }
            }
        } catch (CoreException e) {
            throw new ExecutionException("Error while adding conditional breakpoint", e);
        }

        return null;
    }

}
