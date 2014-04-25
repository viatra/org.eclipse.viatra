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

/**
 * A {@link IDebugEventSetListener} implementation which only listens for 
 * SUSPEND and STEP_END events and notifies all the registered {@link IncQueryDebugEventSetListener}s 
 * when the source of the events is a {@link JDIThread}.
 * 
 * This class is not used at the moment.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings(value = "all")
public class IncQueryDebugEventSetProcessor implements IDebugEventSetListener {

    private static IncQueryDebugEventSetProcessor instance;
    private List<IncQueryDebugEventSetListener> listeners;

    public static IncQueryDebugEventSetProcessor getInstance() {
        if (instance == null) {
            instance = new IncQueryDebugEventSetProcessor();
        }
        return instance;
    }

    protected IncQueryDebugEventSetProcessor() {
        this.listeners = new LinkedList<IncQueryDebugEventSetListener>();
    }

    public void addListener(IncQueryDebugEventSetListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(IncQueryDebugEventSetListener listener) {
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
                        for (IncQueryDebugEventSetListener listener : listeners) {
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
