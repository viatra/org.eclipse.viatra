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
package org.eclipse.viatra.transformation.debug.model;

import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TransformationState {

    private Set<Activation<?>> activations;
    private Set<RuleSpecification<?>> rules;
    private Activation<?> nextActivation;
    
    private ViatraQueryEngine engine;

    private String id;

    public TransformationState(String id, ViatraQueryEngine engine) {
        this.id = id;
        this.engine = engine;
        activations = Sets.newHashSet();
    }
    
    public TransformationState(String id, ViatraQueryEngine engine, Set<Activation<?>> activations, Set<RuleSpecification<?>> rules, Activation<?> nextActivation) {
        this(id, engine);
        this.activations = Sets.newHashSet(activations);
        this.rules = Sets.newHashSet(rules);
        this.nextActivation = nextActivation;
    }

    public void displayNextActivation(Activation<?> act) {
        nextActivation = act;
    }

    public List<Activation<?>> getActivations() {
        return Lists.newArrayList(activations);
    }
    
    public List<Activation<?>> getActivations(RuleSpecification<?> spec) {
        List<Activation<?>> specActivations = Lists.newArrayList();
        for (Activation<?> activation : activations) {
            if(activation.getInstance().getSpecification().equals(spec)){
                specActivations.add(activation);
            }
        }
        return specActivations;
    }
    
    public List<RuleSpecification<?>> getRules() {
        return Lists.newArrayList(rules);
    }

    public String getId() {
        return id;
    }
    
    public void ruleAdded(RuleSpecification<?> rule) {
        rules.add(rule);
    }

    public void ruleRemoved(RuleSpecification<?> rule) {
        rules.remove(rule);
    }

    public void activationCreated(Activation<?> activation) {
        activations.add(activation);
    }

    public void activationFired(Activation<?> activation) {
        activations.remove(activation);
    }
  
    public Activation<?> getNextActivation() {
        return nextActivation;
    }
    
    public ViatraQueryEngine getEngine() {
        return engine;
    }

    public void dispose() {
        activations.clear();
        nextActivation = null;
    }

}
