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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TransformationStateBuilder {
    private String ID;
    private Set<Pair<RuleSpecification<?>, EventFilter<?>>> rules = Sets.newHashSet();
    
    private Deque<Activation<?>> startedActivations = new ArrayDeque<Activation<?>>();

    private Set<Activation<?>> nextActivations = Sets.newHashSet();
    private Set<Activation<?>> conflictingActivations = Sets.newHashSet();
    
    private ITransformationBreakpointHandler breakpointHit;
    
    private TransformationModelBuilder builder;
    
    private DefaultActivationCoder coder = new DefaultActivationCoder();
    
    public TransformationStateBuilder(TransformationModelBuilder builder){
        this.builder = builder;
    }
    
    public TransformationStateBuilder setID(String iD) {
        ID = iD;
        return this;
    }
    
    public void activationFiring(Activation<?> act){
        startedActivations.push(act);
    }
    
    public void nextActivationChanged(Activation<?> act){
        startedActivations.pop();
        startedActivations.push(act);
    }
    
    public void activationFired(Activation<?> act){
        startedActivations.remove(act);
    }
    
    
    public TransformationStateBuilder setBreakpointHit(ITransformationBreakpointHandler breakpointHit) {
        this.breakpointHit = breakpointHit;
        return this;
    }

    public TransformationStateBuilder addRule(Pair<RuleSpecification<?>, EventFilter<?>> rule) {
        rules.add(rule);
        return this;
    }
    
    public TransformationStateBuilder removeRule(Pair<RuleSpecification<?>, EventFilter<?>> rule) {
        rules.remove(rule);
        return this;
    }

    public TransformationStateBuilder setActivations(Set<Activation<?>> conflictingActivations, Set<Activation<?>> nextActivations) {       
        this.conflictingActivations = Sets.difference(conflictingActivations, nextActivations);
        this.nextActivations = nextActivations;
        return this;
    }
    
    public TransformationState build(){
        TransformationState state = new TransformationState(ID);
        Set<Activation<?>> activations = Sets.newHashSet();
        activations.addAll(nextActivations);
        activations.addAll(conflictingActivations);
        
        //Transformation Rules
        List<TransformationRule> stateRules = Lists.newArrayList();
        for (Pair<RuleSpecification<?>, EventFilter<?>> pair : rules) {
            List<RuleActivation> ruleActivations = Lists.newArrayList();
                        
            for (Activation<?> activation : activations) {
               if(activation.getInstance().getSpecification().equals(pair.getKey())){
                   ruleActivations.add(createActivation(state, activation));
               }
            }
            
            TransformationRule transformationRule = new TransformationRule(pair.getKey().getName(), 
                    !pair.getValue().equals(pair.getKey().createEmptyFilter()), 
                    ruleActivations);
            
            stateRules.add(transformationRule);
        }
        
        //Next Activations
        List<RuleActivation> nextActivationsToAdd = Lists.newArrayList();
        
        for (Activation<?> activation : this.nextActivations) {
            nextActivationsToAdd.add(createActivation(state, activation));
        }
        //Conflicting Activations
        List<RuleActivation> conflictingActivationsToAdd = Lists.newArrayList();
        for (Activation<?> activation : this.conflictingActivations) {
            conflictingActivationsToAdd.add(createActivation(state, activation));
        }
        //Activation Stack
        List<RuleActivation> activationStack = Lists.newArrayList();
        for (Activation<?> activation : this.startedActivations) {
            activationStack.add(createActivation(state, activation));
        }
        
        state.setActivationStack(activationStack);
        state.setBreakpointHit(breakpointHit);
        state.setConflictingActivations(conflictingActivationsToAdd);
        state.setNextActivations(nextActivationsToAdd);
        state.setRules(stateRules);
        
        return state;
    }
    
    private RuleActivation createActivation(TransformationState state, Activation<?> original){
        boolean isNext = original.equals(startedActivations.peek());
        
        ActivationTrace trace = coder.createActivationCode(original);
        List<ActivationParameter> parameters = getParameters(original);
        
        return new RuleActivation(trace, isNext, original.getState().toString(), original.getInstance().getSpecification().getName(), parameters, state);
        
    }
    
    private List<ActivationParameter> getParameters(Activation<?> original){
        List<ActivationParameter> parameters = Lists.newArrayList();
        Object atom = original.getAtom();
        if(atom instanceof IPatternMatch){
            IPatternMatch match = (IPatternMatch) atom;
            List<String> parameterNames = match.parameterNames();

            for (String parameterName : parameterNames) {
                Object parameter = match.get(parameterName);
                if(parameter instanceof EObject){
                    parameters.add(new ActivationParameter(builder.getTransformationElement((EObject) parameter), parameterName));
                }
                
            }
        }
        return parameters;
    }
}
