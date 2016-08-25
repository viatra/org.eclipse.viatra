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
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

import com.google.common.collect.Lists;

public class DebuggerHostEndpoint implements IDebuggerHostAgent, IDebuggerHostEndpoint, NotificationListener {
    private String name;
    private List<IDebuggerHostAgentListener> listeners = Lists.newArrayList();
    private DebuggerTargetEndpointMBean mbeanProxy;
    private ObjectName mbeanName;
    private MBeanServerConnection mbsc;
    private JMXConnector jmxc;
    
    public DebuggerHostEndpoint(String ID){
        this.name = ID;
        JMXServiceURL url;
        try {
            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
            jmxc = JMXConnectorFactory.connect(url, null);
            mbsc = jmxc.getMBeanServerConnection();
            mbsc.queryNames(null, null);
            mbeanName = new ObjectName(name);
            mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, 
                    DebuggerTargetEndpointMBean.class, true);
            mbsc.addNotificationListener(mbeanName, this, null, null);
            
        } catch (IOException | MalformedObjectNameException | InstanceNotFoundException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
        }
    }
    
    @Override
    public void sendStepMessage() {
        mbeanProxy.stepForward();
        
    }

    @Override
    public void sendContinueMessage() {
        mbeanProxy.continueExecution();
        
    }

    @Override
    public void sendNextActivationMessage(ActivationTrace activation) {
        mbeanProxy.setNextActivation(activation);
    }

    @Override
    public void sendAddBreakpointMessage(ITransformationBreakpoint breakpoint) {
        mbeanProxy.addBreakpoint(breakpoint);
    }

    @Override
    public void sendRemoveBreakpointMessage(ITransformationBreakpoint breakpoint) {
        mbeanProxy.removeBreakpoint(breakpoint);
    }

    @Override
    public void sendDisableBreakpointMessage(ITransformationBreakpoint breakpoint) {
        mbeanProxy.disableBreakpoint(breakpoint);
    }

    @Override
    public void sendEnableBreakpointMessage(ITransformationBreakpoint breakpoint) {
        mbeanProxy.enableBreakpoint(breakpoint);
    }

    @Override
    public void sendDisconnectMessage() {
        mbeanProxy.disconnect();
    }

    @Override
    public void registerDebuggerHostAgentListener(IDebuggerHostAgentListener listener) {
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    @Override
    public void unRegisterDebuggerHostAgentListener(IDebuggerHostAgentListener listener) {
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
    public void terminated() throws CoreException {
        for (IDebuggerHostAgentListener listener : listeners) {
            listener.terminated(this);
        }
        listeners.clear();
        try {
            mbsc.removeNotificationListener(mbeanName, this, null, null);
            jmxc.close();
        } catch (InstanceNotFoundException | ListenerNotFoundException | IOException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
        }
    }

    
    //JMX CLientListener
    @Override
    public void handleNotification(Notification notification, Object arg1) {
        if (notification instanceof AttributeChangeNotification) {
            //Transformation State changed
            AttributeChangeNotification acn =
                (AttributeChangeNotification) notification;
            if(acn.getAttributeType().equals("TransformationState")){
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream((byte[])acn.getNewValue()));
                    TransformationState state = (TransformationState) objectInputStream.readObject();
                    transformationStateChanged(state);
                } catch (IOException | ClassNotFoundException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
                }
            }

        }else{
           String message = notification.getType();
            if(message.equals(DebuggerTargetEndpoint.TERMINATED)){
                try {
                    terminated();
                } catch (CoreException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public List<TransformationModelElement> getRootElements(){
        return mbeanProxy.getRootElements();
        
    }

    @Override
    public Map<String, List<TransformationModelElement>> getChildren(TransformationModelElement parent){
        return mbeanProxy.getChildren(parent);
    }

    
    @Override
    public Map<String, List<TransformationModelElement>> getCrossReferences(TransformationModelElement parent) {
        return mbeanProxy.getCrossReferences(parent);
    }
}
