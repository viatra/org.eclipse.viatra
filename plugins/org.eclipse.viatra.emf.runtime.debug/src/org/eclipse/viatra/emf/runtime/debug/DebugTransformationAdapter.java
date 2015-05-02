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
package org.eclipse.viatra.emf.runtime.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.viatra.emf.runtime.adapter.impl.AbstractTransformationAdapter;

import com.google.common.collect.Lists;

/**
 * Adapter implementation that enables the user to define breakpoints in a VIATRA based event driven transformation.
 * Once one of these breakpoints is reached, the execution of rule activations is suspended. Then the user can either
 * continue the execution or advance the transformation to the next activation
 * 
 * @author Lunk Péter
 *
 */
public class DebugTransformationAdapter extends AbstractTransformationAdapter{
    protected List<TransformationBreakPoint> breakPoints;
    protected boolean isStep = false;
    
    public DebugTransformationAdapter() {
        breakPoints = Lists.newArrayList();
    }
    
    public DebugTransformationAdapter(List<TransformationBreakPoint> breakpoints) {
        this.breakPoints = breakpoints;
    }

    @Override
    public void onFiring(Activation<?> activation) {
        if (activation != null && (isBreakpoint(activation) || isStep)) {
            while (true) {
                System.out.println("activation: " + activation.toString());
                System.out.println("ruleSpec: " + activation.getInstance().getSpecification().toString());
                System.out.println("enabled: " + activation.isEnabled());
                System.out.println(" Type \"step\" to step or \"continue\" to continue execution");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    String input = bufferedReader.readLine();
                    if (input.toLowerCase().equals("step")) {
                        isStep = true;
                        break;
                    } else if (input.toLowerCase().equals("continue")) {
                        isStep = false;
                        break;
                    } else {
                        System.out.println("Inapropriate input");
                    }
                } catch (IOException e) {
                    System.out.println("Inapropriate input");
                }
            }

        }
        
    }

    private boolean isBreakpoint(Activation<?> activation){
        for (TransformationBreakPoint breakpoint : breakPoints) {
            if(breakpoint.matchingActivation(activation)){
                return true;
            }
        }
        return false;
    }
    
    public void addBreakPoint(TransformationBreakPoint breakpoint){
        breakPoints.add(breakpoint);
    }
        
    public void clearBreakPoints(){
        breakPoints.clear();
    }
    
    public void removeBreakPoint(TransformationBreakPoint breakPoint){
        breakPoints.remove(breakPoint);
    }
    
    public List<TransformationBreakPoint> getBreakPoints(){
        return breakPoints;
    }    
}
