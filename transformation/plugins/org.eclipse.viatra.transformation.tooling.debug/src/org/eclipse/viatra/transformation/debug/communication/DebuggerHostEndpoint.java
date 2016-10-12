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

import static org.eclipse.viatra.transformation.debug.communication.DebuggerCommunicationConstants.TERMINATED;
import static org.eclipse.viatra.transformation.debug.communication.DebuggerCommunicationConstants.URL_HEAD;
import static org.eclipse.viatra.transformation.debug.communication.DebuggerCommunicationConstants.URL_TAIL;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.activator.TransformationDebugActivator;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DebuggerHostEndpoint implements IDebuggerHostAgent, IDebuggerHostEndpoint, NotificationListener {
    public static final String COMM_ERROR_MSG = "Communication with the VIATRA Debugger Agent has been interrupted. Perhaps the target application has been closed abruptly or was not running at all.";
    public static final String COMM_ERROR_TITLE = "Debugger Connection Interrupted";
    public static final String BRKP_ERROR_MSG = "Make sure that the required UI components are loaded before trying to add conditional breakpoints, otherwise such breakpoints will have no effect at all.";
    public static final String BRKP_ERROR_TITLE = "An error occured while adding breakpoint";
    private String name;
    private List<IDebuggerHostAgentListener> listeners = Lists.newArrayList();
    private DebuggerTargetEndpointMBean mbeanProxy;
    private ObjectName mbeanName;
    private MBeanServerConnection mbsc;
    private JMXConnector jmxc;
    
    public DebuggerHostEndpoint(String ID){
        this.name = ID;
        
    }
    
    public void connectTo(int port) throws IOException, MalformedObjectNameException, InstanceNotFoundException{
        JMXServiceURL url = new JMXServiceURL(URL_HEAD+port+URL_TAIL);
        jmxc = JMXConnectorFactory.connect(url, null);
        mbsc = jmxc.getMBeanServerConnection();
        mbsc.queryNames(null, null);
        mbeanName = new ObjectName(name);
        mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, 
                DebuggerTargetEndpointMBean.class, true);
        mbsc.addNotificationListener(mbeanName, this, null, null);
        jmxc.addConnectionNotificationListener(this, null, null);
    }
    
    @Override
    public void sendStepMessage() {
        try {
            mbeanProxy.stepForward();
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        }
        
    }

    @Override
    public void sendContinueMessage() {
        try {
            mbeanProxy.continueExecution();
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        }
        
    }

    @Override
    public void sendNextActivationMessage(ActivationTrace activation) {
        try {
            mbeanProxy.setNextActivation(activation);
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        }
    }

    @Override
    public void sendAddBreakpointMessage(ITransformationBreakpointHandler breakpoint) {
        try {
            mbeanProxy.addBreakpoint(breakpoint);
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        } catch (ViatraDebuggerException e) {
            handleConditinalBreakpointError(e);
        }
    }

    @Override
    public void sendRemoveBreakpointMessage(ITransformationBreakpointHandler breakpoint) {
        try {
            mbeanProxy.removeBreakpoint(breakpoint);
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        } catch (ViatraDebuggerException e) {
            handleConditinalBreakpointError(e);
        }
    }

    @Override
    public void sendDisableBreakpointMessage(ITransformationBreakpointHandler breakpoint) {
        try {
            mbeanProxy.disableBreakpoint(breakpoint);
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        } catch (ViatraDebuggerException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().debug(e.getMessage());
        }
    }

    @Override
    public void sendEnableBreakpointMessage(ITransformationBreakpointHandler breakpoint) {
        try {
            mbeanProxy.enableBreakpoint(breakpoint);
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        } catch (ViatraDebuggerException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().debug(e.getMessage());
        }
    }

    @Override
    public void sendDisconnectMessage() {
        try {
            mbeanProxy.disconnect();
        } catch (InstanceNotFoundException | IOException e) {
            handleCommunicationError(e);
        }
    }

    @Override
    public synchronized void registerDebuggerHostAgentListener(IDebuggerHostAgentListener listener) {
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    @Override
    public synchronized void unRegisterDebuggerHostAgentListener(IDebuggerHostAgentListener listener) {
        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    @Override
    public String getID() {
        return name;
    }

    
    //HostEndpoint
    
    @Override
    public void transformationStateChanged(TransformationState state) {
        for (IDebuggerHostAgentListener listener : listeners) {
            listener.transformationStateChanged(state);
        }
    }

    @Override
    public void terminated() throws ViatraDebuggerException {
        try {
            mbsc.removeNotificationListener(mbeanName, this, null, null);
            jmxc.close();
            terminateListeners();
        } catch (InstanceNotFoundException | ListenerNotFoundException | IOException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().debug(e.getMessage());
            throw new ViatraDebuggerException(e.getMessage());
        }
        
    }

    private void terminateListeners(){
        for (IDebuggerHostAgentListener listener : listeners) {
            listener.terminated(this);
        }
        listeners.clear();
    }

    
    //JMX CLientListener
    @Override
    public void handleNotification(Notification notification, Object arg1) {
        if (notification instanceof AttributeChangeNotification) {
            // Transformation State changed
            AttributeChangeNotification acn = (AttributeChangeNotification) notification;
            if (acn.getAttributeType().equals("TransformationState")) {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            new ByteArrayInputStream((byte[]) acn.getNewValue()));
                    TransformationState state = (TransformationState) objectInputStream.readObject();
                    transformationStateChanged(state);
                } catch (IOException | ClassNotFoundException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage());
                }
            }

        } else if (notification instanceof JMXConnectionNotification) {
            // connection lost
            String message = notification.getType();
            // Transformation State changed
            if (message.equals(JMXConnectionNotification.CLOSED)) {
                terminateListeners();
            }

        } else {
            String message = notification.getType();
            if (message.equals(TERMINATED)) {
                try {
                    terminated();
                } catch (ViatraDebuggerException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public List<TransformationModelElement> getRootElements(){
        try {
            return mbeanProxy.getRootElements();
        } catch (InstanceNotFoundException | IOException e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public Map<String, List<TransformationModelElement>> getChildren(TransformationModelElement parent){
        try {
            return mbeanProxy.getChildren(parent);
        } catch (InstanceNotFoundException | IOException e) {
            return Maps.newHashMap();
        }
    }
    
    @Override
    public Map<String, List<TransformationModelElement>> getCrossReferences(TransformationModelElement parent) {
        try {
            return mbeanProxy.getCrossReferences(parent);
        } catch (InstanceNotFoundException | IOException e) {
            return Maps.newHashMap();
        }
    }
    
    private void handleCommunicationError(final Exception e) {
        terminateListeners();
        ViatraQueryLoggingUtil.getDefaultLogger().error("Communication error: "+e.getMessage());
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                ErrorDialog.openError(activeShell, COMM_ERROR_TITLE, COMM_ERROR_MSG, new Status(Status.ERROR, TransformationDebugActivator.PLUGIN_ID, e.getMessage())); 
            }
        });
    }
    
    private void handleConditinalBreakpointError(final ViatraDebuggerException e) {
        terminateListeners();
        ViatraQueryLoggingUtil.getDefaultLogger().error("Invalid Conditional Breakpoint: "+e.getMessage());
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                ErrorDialog.openError(activeShell, BRKP_ERROR_TITLE, BRKP_ERROR_MSG, new Status(Status.ERROR, TransformationDebugActivator.PLUGIN_ID, e.getMessage())); 
            }
        });
    }
}
