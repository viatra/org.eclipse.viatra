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

import java.io.Serializable;
import java.util.List;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;

import com.google.common.collect.Lists;

public class TransformationState implements Serializable{
    private static final long serialVersionUID = 6702356275765247363L;
    private final String ID;
    private List<TransformationRule> rules;
    private List<RuleActivation> activationStack;
    
    private List<RuleActivation> nextActivations;
    private List<RuleActivation> conflictingActivations;
    private List<ITransformationBreakpointHandler> breakpoints;
    private ITransformationBreakpointHandler breakpointHit;
    
    
    public TransformationState(String iD) {
        super();
        ID = iD;
    }
      
    public List<RuleActivation> getActivationStack() {
        return Lists.newArrayList(activationStack);
    }
        
    public String getID() {
        return ID;
    }
    
    public List<TransformationRule> getRules() {
        return Lists.newArrayList(rules);
    }
    
    public List<RuleActivation> getNextActivations() {
        return Lists.newArrayList(nextActivations);
    }
    
    public List<RuleActivation> getConflictingActivations() {
        return Lists.newArrayList(conflictingActivations);
    }
    
    public List<ITransformationBreakpointHandler> getBreakpoints() {
        return Lists.newArrayList(breakpoints);
    }

    public ITransformationBreakpointHandler getBreakpointHit() {
        return breakpointHit;
    }

    protected void setRules(List<TransformationRule> rules) {
        this.rules = rules;
    }

    protected void setActivationStack(List<RuleActivation> activationStack) {
        this.activationStack = activationStack;
    }

    protected void setNextActivations(List<RuleActivation> nextActivations) {
        this.nextActivations = nextActivations;
    }

    protected void setConflictingActivations(List<RuleActivation> conflictingActivations) {
        this.conflictingActivations = conflictingActivations;
    }

    protected void setBreakpoints(List<ITransformationBreakpointHandler> breakpoints) {
        this.breakpoints = breakpoints;
    }

    protected void setBreakpointHit(ITransformationBreakpointHandler breakpointHit) {
        this.breakpointHit = breakpointHit;
    }    
}
