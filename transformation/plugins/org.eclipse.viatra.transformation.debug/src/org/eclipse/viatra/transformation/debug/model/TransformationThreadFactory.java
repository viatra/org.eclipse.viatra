/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.model;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.transformation.debug.TransformationDebugger;

import com.google.common.collect.Lists;

public enum TransformationThreadFactory {
    INSTANCE;
    
    private List<TransformationThread> threads = Lists.newArrayList();
    private List<ITransformationStateListener> listenersToAdd = Lists.newArrayList();
    
    public TransformationThread createTransformationThread(TransformationDebugTarget target, TransformationDebugger debugger, IType transformationType){
        TransformationThread thread = new TransformationThread(target, debugger, debugger.getId(), transformationType);
        if(!threads.contains(thread)){
            threads.add(thread);
        }
        for (ITransformationStateListener iTransformationStateListener : listenersToAdd) {
            thread.registerTransformationStateListener(iTransformationStateListener); 
        }
        return thread;
    }
    
    public TransformationThread getTransformationThread(String id){
        for (TransformationThread thread : threads) {
            try {
                if(thread.getName().equals(id)){
                    return thread;
                }
            } catch (DebugException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public List<TransformationThread> getTransformationThreads(){
        return threads;
    }
    
    public void deleteTransformationThread(TransformationThread thread){
        if(threads.contains(thread)){
            threads.remove(thread);
        }
    }
    
    public void addListener(ITransformationStateListener listener){
        if(!listenersToAdd.contains(listener)){
            listenersToAdd.add(listener);
        }
        for (TransformationThread thread : threads) {
            thread.registerTransformationStateListener(listener);
        }
    }
    
    public void unRegisterListener(ITransformationStateListener listener){
        if(listenersToAdd.contains(listener)){
            listenersToAdd.remove(listener);
        }
        for (TransformationThread thread : threads) {
            thread.unRegisterTransformationStateListener(listener);
        }
    }
    
    
}
