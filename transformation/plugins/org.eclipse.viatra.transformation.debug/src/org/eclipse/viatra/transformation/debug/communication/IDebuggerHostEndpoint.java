package org.eclipse.viatra.transformation.debug.communication;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public interface IDebuggerHostEndpoint {
    public String getID();
    
    public void transformationStateChanged(TransformationState state);
    
    public void terminated() throws CoreException;
}
