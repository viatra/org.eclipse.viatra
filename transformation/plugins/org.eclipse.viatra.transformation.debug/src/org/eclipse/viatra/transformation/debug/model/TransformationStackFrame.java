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

import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.Activation;

import com.google.common.collect.Lists;

public class TransformationStackFrame extends TransformationDebugElement implements IStackFrame{
    private TransformationThread thread;
    private String name;
    private IVariable[] variables;
    
    
    public TransformationStackFrame(TransformationThread thread, Activation<?> activation) throws DebugException {
        super((TransformationDebugTarget) thread.getDebugTarget());
        this.thread = thread;
        this.name = activation.getInstance().getSpecification().getName()+" : "+activation.getState();
                
        List<TransformationVariable> transformationVariables = Lists.newArrayList();
        Object atom = activation.getAtom();
        if(atom instanceof IPatternMatch){
            IPatternMatch match = (IPatternMatch) atom;
            transformationVariables.addAll(createVariables(match));
        }
        
        this.variables = (IVariable[]) transformationVariables.toArray(new TransformationVariable[0]); 
    }
    
    private List<TransformationVariable> createVariables(IPatternMatch match){
        List<TransformationVariable> createdVariables = Lists.newArrayList();
        List<String> parameterNames = match.parameterNames();

        for (String parameterName : parameterNames) {
            Object parameter = match.get(parameterName);
            TransformationValue value = new TransformationValue((TransformationDebugTarget) getDebugTarget(), parameter);
            TransformationVariable variable = new TransformationVariable((TransformationDebugTarget) getDebugTarget(), parameterName, value);
            createdVariables.add(variable);
        }
        return createdVariables;
    }
    
    @Override
    public boolean canStepInto() {
        return thread.canStepInto();
    }

    @Override
    public boolean canStepOver() {
        return thread.canStepOver();
    }

    @Override
    public boolean canStepReturn() {
        return thread.canStepReturn();
    }

    @Override
    public boolean isStepping() {
        return thread.isStepping();
    }

    @Override
    public void stepInto() throws DebugException {
        thread.stepInto();
    }

    @Override
    public void stepOver() throws DebugException {
        thread.stepOver();
    }

    @Override
    public void stepReturn() throws DebugException {
        thread.stepReturn();
    }

    @Override
    public boolean canResume() {
        return thread.canResume();
    }

    @Override
    public boolean canSuspend() {
        return thread.canSuspend();
    }

    @Override
    public boolean isSuspended() {
        return thread.isSuspended();
    }

    @Override
    public void resume() throws DebugException {
        thread.resume();
    }

    @Override
    public void suspend() throws DebugException {
        thread.suspend();
    }

    @Override
    public boolean canTerminate() {
        return thread.canTerminate();
    }

    @Override
    public boolean isTerminated() {
        return thread.isTerminated();
    }

    @Override
    public void terminate() throws DebugException {
        thread.terminate();
    }

    @Override
    public IThread getThread() {
        return thread;
    }

    @Override
    public IVariable[] getVariables() throws DebugException {
        return Arrays.copyOf(variables, variables.length);
    }

    @Override
    public boolean hasVariables() throws DebugException {
        return variables.length > 0;
    }

    @Override
    public int getLineNumber() throws DebugException {
        return -1;
    }

    @Override
    public int getCharStart() throws DebugException {
        return -1;
    }

    @Override
    public int getCharEnd() throws DebugException {
        return -1;
    }

    @Override
    public String getName() throws DebugException {
        return name;
    }

    @Override
    public IRegisterGroup[] getRegisterGroups() throws DebugException {
        return new IRegisterGroup[0];
    }

    @Override
    public boolean hasRegisterGroups() throws DebugException {
        return false;
    }
}
