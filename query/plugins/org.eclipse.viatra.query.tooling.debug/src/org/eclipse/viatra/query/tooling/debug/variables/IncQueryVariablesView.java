/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
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
public class IncQueryVariablesView extends VariablesView {

    public static final String ID = "org.eclipse.viatra.query.tooling.debug.variables.IncQueryVariablesView";

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
