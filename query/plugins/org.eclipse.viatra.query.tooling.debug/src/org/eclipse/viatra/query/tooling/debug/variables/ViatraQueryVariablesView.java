/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.debug.variables;

import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.tooling.debug.common.StackFrameWrapper;

/**
 * A Debug Variables View that displays the {@link ViatraQueryEngine}s in the JVM.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public class ViatraQueryVariablesView extends VariablesView {

    public static final String ID = "org.eclipse.viatra.query.tooling.debug.variables.ViatraQueryVariablesView";

    @Override
    protected void setViewerInput(Object context) {
        if (context instanceof JDIStackFrame) {
            StackFrameWrapper wrapper = StackFrameWrapper.transform((JDIStackFrame) context);
            if (wrapper != null) {
                wrapper.setVariablesFactory(new DebugVariablesFactory());
                super.setViewerInput(wrapper);
            }
        } else {
            super.setViewerInput(context);
        }
    }
}
