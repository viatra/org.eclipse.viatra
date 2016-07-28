package org.eclipse.viatra.transformation.debug.communication;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace;

public interface IDebuggerTargetEndpoint {
    public void stepForward();
    public void continueExecution();
    
    public void setNextActivation(ActivationTrace activation);

    public void addBreakpoint(ITransformationBreakpoint breakpoint);
    public void removeBreakpoint(ITransformationBreakpoint breakpoint);
    public void disableBreakpoint(ITransformationBreakpoint breakpoint);
    public void enableBreakpoint(ITransformationBreakpoint breakpoint);
    
    public void disconnect();
    
    public String getID();
    
}
