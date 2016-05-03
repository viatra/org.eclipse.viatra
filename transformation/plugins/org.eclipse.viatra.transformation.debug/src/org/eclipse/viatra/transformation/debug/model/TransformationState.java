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

import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class TransformationState {

    private Set<Pair<RuleSpecification<?>, EventFilter<?>>> rules;
    private Activation<?> nextActivation;
    private List<Activation<?>> nextActivations;
    private List<Activation<?>> conflictingActivations;
    private Set<Activation<?>> newActivations;

    private ViatraQueryEngine engine;

    private String id;

    public TransformationState(String id, ViatraQueryEngine engine) {
        this.id = id;
        nextActivations = Lists.newArrayList();
        conflictingActivations = Lists.newArrayList();
        newActivations = Sets.newHashSet();
        this.engine = engine;
    }

    public TransformationState(String id, ViatraQueryEngine engine, Set<Activation<?>> nextActivations,
            Set<Activation<?>> conflictingActivations, Set<Pair<RuleSpecification<?>, EventFilter<?>>> rules,
            Activation<?> nextActivation) {
        this(id, engine);
        this.nextActivations = Lists.newArrayList(nextActivations);
        this.conflictingActivations = Lists.newArrayList(conflictingActivations);
        this.newActivations.addAll(nextActivations);
        this.newActivations.addAll(conflictingActivations);
        this.rules = Sets.newHashSet(rules);
        this.nextActivation = nextActivation;
    }

    public void activationFiring(Activation<?> act) {
        nextActivation = act;
    }

    public void clearNewActivations() {
        newActivations.clear();
    }

    public void updateActivations(Set<Activation<?>> nextActivations, Set<Activation<?>> conflictingActivations) {
        SetView<Activation<?>> addedElements = Sets.difference(conflictingActivations,
                Sets.newHashSet(this.conflictingActivations));
        SetView<Activation<?>> removedElements = Sets.difference(Sets.newHashSet(this.conflictingActivations),
                conflictingActivations);

        newActivations.addAll(addedElements);
        newActivations.removeAll(removedElements);

        this.nextActivations = Lists.newArrayList(nextActivations);
        this.conflictingActivations = Lists.newArrayList(conflictingActivations);
    }

    public List<Pair<RuleSpecification<?>, EventFilter<?>>> getRules() {
        return Lists.newArrayList(rules);
    }

    public String getId() {
        return id;
    }

    public void ruleAdded(Pair<RuleSpecification<?>, EventFilter<?>> rule) {
        rules.add(rule);
    }

    public void ruleRemoved(Pair<RuleSpecification<?>, EventFilter<?>> rule) {
        rules.remove(rule);
    }

    public List<Activation<?>> getNextActivations() {
        return nextActivations;
    }

    public List<Activation<?>> getConflictingActivations() {
        return conflictingActivations;
    }

    public List<Activation<?>> getNotExecutableActivations() {
        List<Activation<?>> retVal = Lists.newArrayList(conflictingActivations);
        retVal.removeAll(getNextActivations());

        return retVal;
    }

    public List<Activation<?>> getConflictingActivations(Pair<RuleSpecification<?>, EventFilter<?>> pair) {
        List<Activation<?>> specActivations = Lists.newArrayList();
        for (Activation<?> activation : conflictingActivations) {
            if (activation.getInstance().getSpecification().equals(pair.getKey())) {
                specActivations.add(activation);
            }
        }
        return specActivations;
    }

    public Activation<?> getNextActivation() {
        return nextActivation;
    }

    public ViatraQueryEngine getEngine() {
        return engine;
    }

    public Set<Activation<?>> getNewActivations() {
        return newActivations;
    }

    public void dispose() {
        nextActivations.clear();
        conflictingActivations.clear();
    }
    
    protected void setNextActivation(Activation<?> nextActivation) {
        this.nextActivation = nextActivation;
    }
}
