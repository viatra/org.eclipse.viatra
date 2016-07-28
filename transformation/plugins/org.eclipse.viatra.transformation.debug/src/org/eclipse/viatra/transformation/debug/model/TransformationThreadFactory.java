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
package org.eclipse.viatra.transformation.debug.model;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;

import com.google.common.collect.Lists;

public class TransformationThreadFactory {
    private static TransformationThreadFactory instance;
    private List<TransformationThread> threads;
    
    protected TransformationThreadFactory(){
        threads = Lists.newArrayList();
    }

    public static TransformationThreadFactory getInstance() {
        if(instance == null){
            instance = new TransformationThreadFactory();
        }
        return instance;
    }
    
    public TransformationThread createTransformationThread(String name, IDebuggerHostAgent agent, TransformationDebugTarget target, IType transformationClass){
        TransformationThread thread = new TransformationThread(name, agent, target, transformationClass);
                
        threads.add(thread);
        return thread;
    }
    
    public TransformationThread getTransformationThread(String id) throws DebugException{
        for (TransformationThread thread : threads) {
            try {
                if(thread.getName().equals(id)){
                    return thread;
                }
            } catch (DebugException e) {
                throw e;
            }
        }
        return null;
    }
        
    public void deleteTransformationThread(TransformationThread thread){
        if(threads.contains(thread)){
            threads.remove(thread);
        }
    }
        
    public List<TransformationThread> getTransformationThreads(){
        return threads;
    }
    
}
