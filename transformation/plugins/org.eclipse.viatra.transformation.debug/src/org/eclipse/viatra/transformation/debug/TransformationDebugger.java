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
package org.eclipse.viatra.transformation.debug;

import java.util.List;

import org.eclipse.viatra.transformation.debug.adapter.impl.AbstractTransformationAdapter;
import org.eclipse.viatra.transformation.debug.breakpoints.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.breakpoints.impl.TransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.controller.IDebugController;
import org.eclipse.viatra.transformation.evm.api.Activation;

import com.google.common.collect.Lists;

/**
 * Adapter implementation that enables the user to define breakpoints in a VIATRA based event driven transformation.
 * Once one of these breakpoints is reached, the execution of rule activations is suspended. Then the user can either
 * continue the execution or advance the transformation to the next activation
 * 
 * @author Peter Lunk
 *
 */
public class TransformationDebugger extends AbstractTransformationAdapter{
    private IDebugController ui;
    protected List<ITransformationBreakpoint> breakPoints;
    protected DebuggerActions action = DebuggerActions.Continue;
    
    public TransformationDebugger(IDebugController usedUI) {
        breakPoints = Lists.newArrayList();
        ui = usedUI;
    }
    
    public TransformationDebugger(List<ITransformationBreakpoint> breakpoints, IDebugController usedUI) {
        this.breakPoints = breakpoints;
        ui = usedUI;
    }

    @Override
    public Activation<?> beforeFiring(Activation<?> activation) {
        if (activation != null && (hasBreakpoint(activation) || action == DebuggerActions.Step)) {
            ui.displayTransformationContext(activation);
            action = ui.getDebuggerAction();
        }
        return activation;
    }

    private boolean hasBreakpoint(Activation<?> activation){
        for (ITransformationBreakpoint breakpoint : breakPoints) {
            if(breakpoint.shouldBreak(activation)){
                return true;
            }
        }
        return false;
    }
    
    public void addBreakPoint(TransformationBreakpoint breakpoint){
        breakPoints.add(breakpoint);
    }
        
    public void clearBreakPoints(){
        breakPoints.clear();
    }
    
    public void removeBreakPoint(TransformationBreakpoint breakPoint){
        breakPoints.remove(breakPoint);
    }
    
    public List<ITransformationBreakpoint> getBreakPoints(){
        return breakPoints;
    } 
    
    public enum DebuggerActions{
        Step,
        Continue
    }
}
