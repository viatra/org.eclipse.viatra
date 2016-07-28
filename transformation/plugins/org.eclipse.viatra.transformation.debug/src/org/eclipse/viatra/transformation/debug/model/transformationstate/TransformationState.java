package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.util.List;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;

import com.google.common.collect.Lists;

public class TransformationState {
    private final String ID;
    private List<TransformationRule> rules;
    private List<RuleActivation> activationStack;
    
    private List<RuleActivation> nextActivations;
    private List<RuleActivation> conflictingActivations;
    private List<ITransformationBreakpoint> breakpoints;
    private ITransformationBreakpoint breakpointHit;
    
    
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
    
    public List<ITransformationBreakpoint> getBreakpoints() {
        return Lists.newArrayList(breakpoints);
    }

    public ITransformationBreakpoint getBreakpointHit() {
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

    protected void setBreakpoints(List<ITransformationBreakpoint> breakpoints) {
        this.breakpoints = breakpoints;
    }

    protected void setBreakpointHit(ITransformationBreakpoint breakpointHit) {
        this.breakpointHit = breakpointHit;
    }    
}
