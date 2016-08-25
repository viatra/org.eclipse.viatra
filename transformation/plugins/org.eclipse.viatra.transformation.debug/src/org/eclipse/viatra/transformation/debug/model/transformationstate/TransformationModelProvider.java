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
package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;

public class TransformationModelProvider {
    private IDebuggerHostAgent agent;
    
    
    public TransformationModelProvider(IDebuggerHostAgent agent){
        this.agent = agent;
    }
    
    
    public void loadElementContent(TransformationModelElement element){
        Map<String, List<TransformationModelElement>> crossReferences = agent.getCrossReferences(element);
        Map<String, List<TransformationModelElement>> children = agent.getChildren(element);
        
        
        element.setCrossReferences(crossReferences);
        element.setContainedElements(children);
    }
    
    public List<TransformationModelElement> getRootElements(){
        return agent.getRootElements();
    }
}
