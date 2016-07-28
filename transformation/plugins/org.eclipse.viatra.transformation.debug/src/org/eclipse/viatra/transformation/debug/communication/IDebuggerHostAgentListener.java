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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public interface IDebuggerHostAgentListener {
    
    public void transformationStateChanged(TransformationState state);
        
    public void terminated(IDebuggerHostAgent agent) throws CoreException;
}
