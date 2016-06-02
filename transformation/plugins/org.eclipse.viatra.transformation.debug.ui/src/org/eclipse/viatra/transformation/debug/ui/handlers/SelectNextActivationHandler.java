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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.ui.util.DebugUIUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;

public class SelectNextActivationHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (selection instanceof IStructuredSelection
                && ((IStructuredSelection) selection).getFirstElement() instanceof Activation<?>) {
            TransformationThread thread = DebugUIUtil
                    .getActivationThread((Activation<?>) ((IStructuredSelection) selection).getFirstElement());
            if (thread != null) {
                thread.setNextActivation((Activation<?>) ((IStructuredSelection) selection).getFirstElement());
            }
        }

        return null;
    }

}
