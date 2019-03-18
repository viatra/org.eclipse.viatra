/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.util;

import java.util.List;

import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public class ViatraDebuggerUtil {
    
    private ViatraDebuggerUtil() {/*Utility class constructor*/}
    
    public static TransformationThread getThread(TransformationState state){
        List<TransformationThread> transformationThreads = TransformationThreadFactory.getInstance().getTransformationThreads();
        for (TransformationThread transformationThread : transformationThreads) {
            if(transformationThread.getTransformationState().equals(state)){
                return transformationThread;
            }
        }
        return null;
    }
    
        
}
