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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;
import org.eclipse.viatra.query.tooling.debug.common.IncQueryDebugVariable;
import org.eclipse.viatra.query.tooling.debug.common.VariablesFactory;
import org.eclipse.viatra.query.tooling.debug.variables.values.EngineValue;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

/**
 * This factory is responsible for the creation of {@link IJavaVariable}s for all the available {@link IncQueryEngine}s
 * in the current JVM.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public class DebugVariablesFactory extends VariablesFactory {

    @Override
    public List<IJavaVariable> getVariables(JDIStackFrame wrappedStackFrame, ThreadReference threadReference) {
        List<IJavaVariable> variables = new ArrayList<IJavaVariable>();
        try {
            VirtualMachine vm = getVirtualMachine(wrappedStackFrame);
            if (vm != null) {
                List<EngineValue> engines = new ArrayList<EngineValue>();

                for (ReferenceType rt : vm.allClasses()) {
                    if (rt.name().matches(NameConstants.INCQUERY_ENGINE_IMPL_NAME)) {
                        List<ObjectReference> instances = rt.instances(100);
                        if (instances.size() > 0) {
                            for (ObjectReference instance : instances) {
                                EngineValue value = new EngineValue(wrappedStackFrame.getJavaDebugTarget(),
                                        ValueWrapper.wrap(instance, threadReference));
                                engines.add(value);
                            }
                            break;
                        }
                    }
                }

                // make sure that the engines are ordered by their object identifiers
                Collections.sort(engines);

                for (EngineValue ev : engines) {
                    IncQueryDebugVariable var = new IncQueryDebugVariable(wrappedStackFrame.getJavaDebugTarget());
                    var.setValue(ev);
                    variables.add(var);
                }
            }
        } catch (DebugException e) {
            IncQueryLoggingUtil.getLogger(DebugVariablesFactory.class).error("Couldn't retrieve the list of debug variables!", e);
        }
        return variables;
    }

}
