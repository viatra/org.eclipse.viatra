/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.communication;

public class DebuggerCommunicationConstants {
    
    private DebuggerCommunicationConstants() {/*Utility class constructor*/}
    
    public static final String MBEANNAME = "org.eclipse.viatra.transformation.debug.communication.impl:type=DebuggerTargetEndpoint";
    public static final String CURRENTVERSION = "0.15.0";
    
    public static final String SUSPENDED = "Suspended";
    public static final String TERMINATED = "Terminated";
    
    public static final String URL_TAIL = "/jmxrmi";
    public static final String URL_HEAD = "service:jmx:rmi:///jndi/rmi://localhost:";
}
