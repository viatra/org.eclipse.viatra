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
package org.eclipse.viatra.transformation.debug.ui.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugTarget;
import org.eclipse.viatra.transformation.debug.model.TransformationStackFrame;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.breakpoint.TransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.RuleParameterTrace;

public class DebugModelPresentation extends LabelProvider implements IDebugModelPresentation {

    @Override
    public void setAttribute(String attribute, Object value) {
        // Attributes are not supported
    }

    @Override
    public String getText(Object element) {
        try {
            if (element instanceof TransformationBreakpoint) {
                TransformationBreakpoint breakpoint = (TransformationBreakpoint) element;
                String parameters = "";
                for (RuleParameterTrace parameterTrace : breakpoint.getTrace().getRuleParameterTraces()) {
                    parameters += parameterTrace.getParameterName() + " : " + parameterTrace.getObjectId() + " ";
                }
                return "Rule: " + breakpoint.getTrace().getRuleName() + "(" + parameters + ")";

            } else if (element instanceof TransformationStackFrame) {
                return ((TransformationStackFrame) element).getName();
            } else if (element instanceof TransformationThread) {
                return ((TransformationThread) element).getName();
            } else if (element instanceof TransformationDebugTarget) {
                return ((TransformationDebugTarget) element).getName();
            } 
        } catch (DebugException e) {
            e.printStackTrace();
        }

        return super.getText(element);
    }

    @Override
    public void computeDetail(IValue value, IValueDetailListener listener) {
        // TODO support details for values

    }

    @Override
    public IEditorInput getEditorInput(Object element) {
        // SRC lookup is not supported yet
        return null;
    }

    @Override
    public String getEditorId(IEditorInput input, Object element) {
        // SRC lookup is not supported yet
        return null;
    }

}
