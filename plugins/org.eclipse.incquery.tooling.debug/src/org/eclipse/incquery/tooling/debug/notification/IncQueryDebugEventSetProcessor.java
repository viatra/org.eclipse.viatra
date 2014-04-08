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
package org.eclipse.incquery.tooling.debug.notification;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

@SuppressWarnings(value = "all")
public class IncQueryDebugEventSetProcessor implements IDebugEventSetListener {

    private static IncQueryDebugEventSetProcessor instance;
    private List<org.eclipse.incquery.tooling.debug.notification.IDebugEventSetListener> listeners;

    public static IncQueryDebugEventSetProcessor getInstance() {
        if (instance == null) {
            instance = new IncQueryDebugEventSetProcessor();
        }
        return instance;
    }

    protected IncQueryDebugEventSetProcessor() {
        this.listeners = new LinkedList<org.eclipse.incquery.tooling.debug.notification.IDebugEventSetListener>();
    }

    public void addListener(org.eclipse.incquery.tooling.debug.notification.IDebugEventSetListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(org.eclipse.incquery.tooling.debug.notification.IDebugEventSetListener listener) {
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
                        for (org.eclipse.incquery.tooling.debug.notification.IDebugEventSetListener listener : listeners) {
                            listener.update((JDIStackFrame) thread.getStackFrames()[0]);
                        }
                    } catch (DebugException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
