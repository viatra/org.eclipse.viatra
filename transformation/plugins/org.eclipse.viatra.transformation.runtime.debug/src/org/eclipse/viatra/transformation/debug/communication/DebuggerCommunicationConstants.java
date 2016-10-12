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
package org.eclipse.viatra.transformation.debug.communication;

public class DebuggerCommunicationConstants {
    public static final String MBEANNAME = "org.eclipse.viatra.transformation.debug.communication.impl:type=DebuggerTargetEndpoint";
    public static final String CURRENTVERSION = "0.15.0";
    
    public static final String SUSPENDED = "Suspended";
    public static final String TERMINATED = "Terminated";
    
    public static final String URL_TAIL = "/jmxrmi";
    public static final String URL_HEAD = "service:jmx:rmi:///jndi/rmi://localhost:";
}
