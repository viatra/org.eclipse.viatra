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
import org.eclipse.viatra.transformation.debug.TransformationDebugger;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class TransformationThreadFactory {
    private static TransformationThreadFactory instance;
    private List<TransformationThread> threads;
    private Multimap<String, ITransformationStateListener> listenersToAdd;
    
    protected TransformationThreadFactory(){
        threads = Lists.newArrayList();
        listenersToAdd = ArrayListMultimap.create();
    }

    public static TransformationThreadFactory getInstance() {
        if(instance == null){
            instance = new TransformationThreadFactory();
        }
        return instance;
    }
    
    public TransformationThread createTransformationThread(TransformationDebugTarget target, TransformationDebugger debugger, AdaptableEVM evm, IType transformationType){
        TransformationThread thread = new TransformationThread(target, debugger, evm, transformationType);
        
        for(ITransformationStateListener listener : listenersToAdd.get(evm.getIdentifier())){
            thread.registerTransformationStateListener(listener);
        }
        
        threads.add(thread);
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
    
    public void registerListener(ITransformationStateListener listener, String id){
        TransformationThread transformationThread = getTransformationThread(id);
        if(transformationThread != null){
            transformationThread.registerTransformationStateListener(listener);
        }
        listenersToAdd.put(id, listener);
    }
    
    public void unRegisterListener(ITransformationStateListener listener){
        for (TransformationThread transformationThread : threads) {
            transformationThread.unRegisterTransformationStateListener(listener);
        }
        for(String id : listenersToAdd.keySet()){
            listenersToAdd.remove(id, listener);
        }
    }
    
}
