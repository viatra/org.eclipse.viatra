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
package org.eclipse.viatra.query.tooling.debug.notification;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * A {@link IDebugEventSetListener} implementation which only listens for 
 * SUSPEND and STEP_END events and notifies all the registered {@link ViatraQueryDebugEventSetListener}s 
 * when the source of the events is a {@link JDIThread}.
 * 
 * This class is not used at the moment.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings(value = "all")
public class ViatraDebugEventSetProcessor implements IDebugEventSetListener {

    private static ViatraDebugEventSetProcessor instance;
    private List<ViatraQueryDebugEventSetListener> listeners;

    public static ViatraDebugEventSetProcessor getInstance() {
        if (instance == null) {
            instance = new ViatraDebugEventSetProcessor();
        }
        return instance;
    }

    protected ViatraDebugEventSetProcessor() {
        this.listeners = new LinkedList<ViatraQueryDebugEventSetListener>();
    }

    public void addListener(ViatraQueryDebugEventSetListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ViatraQueryDebugEventSetListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        for (DebugEvent event : events) {
            if (event.getKind() == DebugEvent.SUSPEND || event.getDetail() == DebugEvent.STEP_END) {
                Object source = event.getSource();
                if (source instanceof JDIThread) {
                    JDIThread thread = (JDIThread) source;
                    try {
                        for (ViatraQueryDebugEventSetListener listener : listeners) {
                            listener.update((JDIStackFrame) thread.getStackFrames()[0]);
                        }
                    } catch (DebugException e) {
                        ViatraQueryLoggingUtil.getLogger(ViatraDebugEventSetProcessor.class).error("Couldn't retrieve the stack frames!", e);
                    }
                }
            }
        }
    }
}
