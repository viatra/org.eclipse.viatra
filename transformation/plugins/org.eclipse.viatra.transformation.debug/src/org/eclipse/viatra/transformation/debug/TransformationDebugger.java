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
package org.eclipse.viatra.transformation.debug;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.debug.model.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractEVMListener;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMAdapter;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Adapter implementation that enables the user to define breakpoints in a VIATRA based event driven transformation.
 * Once one of these breakpoints is reached, the execution of rule activations is suspended. Then the user can either
 * continue the execution or advance the transformation to the next activation.
 * 
 * @author Peter Lunk
 *
 */
public class TransformationDebugger extends AbstractEVMListener implements IEVMAdapter {
    private String id;
    private List<ITransformationDebugListener> listeners = Lists.newArrayList();
    private ViatraQueryEngine engine;
    
    //Debug functions
    private List<ITransformationBreakpoint> breakpoints = Lists.newArrayList();
    private Set<Pair<RuleSpecification<?>, EventFilter<?>>> rules = Sets.newHashSet();
    private Set<Activation<?>> nextActivations = Sets.newHashSet();
    private Set<Activation<?>> conflictingActivations = Sets.newHashSet();
    
    private Activation<?> nextActivation;
    
    private DebuggerActions action = DebuggerActions.Continue;
    private boolean firstRun = true;
    private boolean actionSet = false;
    
    public TransformationDebugger(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ViatraQueryEngine getEngine() {
        return engine;
    }

    @Override
    public void initializeListener(ViatraQueryEngine engine) {
        this.engine = engine;
        for (ITransformationDebugListener listener : listeners) {
            listener.started();
        }
    }
    
    @Override
    public void addedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        rules.add(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
        for (ITransformationDebugListener listener : listeners) {
            listener.addedRule(specification, filter);
        }
    }

    @Override
    public void removedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        rules.remove(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
        for (ITransformationDebugListener listener : listeners) {
            listener.removedRule(specification, filter);
            
        }
    }
    
    @Override
    public void afterFiring(Activation<?> activation) {
        for (ITransformationDebugListener listener : listeners) {
            listener.activationFired(activation);
        }
    }

    @Override
    public void disposeListener() {
        for (ITransformationDebugListener listener : listeners) {
            listener.terminated();
        }
        breakpoints.clear();
        listeners.clear();
    }

    @Override
    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set) {
        return new TransformationDebuggerConflictSet(set);
    }
    
    @Override
    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
        return new TransformationDebuggerIterator(iterator);
    };
    
    public class TransformationDebuggerConflictSet implements ChangeableConflictSet {
        private final ChangeableConflictSet delegatedSet;
        
        
        public TransformationDebuggerConflictSet(ChangeableConflictSet delegatedSet){
            this.delegatedSet = delegatedSet;
            conflictSetChanged(this);
        }
        
        @Override
        public Activation<?> getNextActivation() {
            return delegatedSet.getNextActivation();
        }

        @Override
        public Set<Activation<?>> getNextActivations() {
            Set<Activation<?>> nextActivations = delegatedSet.getNextActivations();
            return nextActivations;
        }

        @Override
        public Set<Activation<?>> getConflictingActivations() {
            Set<Activation<?>> conflictingActivations = delegatedSet.getConflictingActivations();
            return conflictingActivations;
        }

        @Override
        public ConflictResolver getConflictResolver() {
            return delegatedSet.getConflictResolver();
        }

        @Override
        public boolean addActivation(Activation<?> activation) {
            boolean result = delegatedSet.addActivation(activation);
            conflictSetChanged(this);
            return result;
                    
        }

        @Override
        public boolean removeActivation(Activation<?> activation) {
            boolean result = delegatedSet.removeActivation(activation);
            conflictSetChanged(this);
            return result;
        }
        
    }

    public class TransformationDebuggerIterator implements Iterator<Activation<?>> {
        private final Iterator<Activation<?>> delegatedIterator;

        public TransformationDebuggerIterator(Iterator<Activation<?>> delegatedIterator) {
            this.delegatedIterator = delegatedIterator;
        }

        @Override
        public boolean hasNext() {
            return delegatedIterator.hasNext();
        }

        @Override
        public Activation<?> next() {
                                    
            nextActivation = delegatedIterator.next();
            
            for (ITransformationDebugListener listener : listeners) {
                listener.activationFiring(nextActivation);
                
            }
            
            if (nextActivation != null && (hasBreakpoint(nextActivation) || action == DebuggerActions.Step)) {

                for (ITransformationDebugListener listener : listeners) {
                    listener.suspended();
                }

                while (!actionSet) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                actionSet = false;
            }
            
            return nextActivation;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Deletion from this iterator is not supported.");

        }

    }

    private boolean hasBreakpoint(Activation<?> activation) {
        for (ITransformationBreakpoint breakpoint : breakpoints) {
            if (breakpoint.shouldBreak(activation)) {
                for (ITransformationDebugListener listener : listeners) {
                    listener.breakpointHit(breakpoint);
                }
                return true;
            }
        }
        if (breakpoints.isEmpty() && firstRun) {
            firstRun = false;
            return true;
        }
        return false;
    }

    public TransformationState registerTransformationDebugListener(ITransformationDebugListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        return new TransformationState(id, engine, nextActivations, conflictingActivations, rules, nextActivation);
    }

    public void unRegisterTransformationDebugListener(ITransformationDebugListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void addBreakpoint(ITransformationBreakpoint breakpoint) {
        if (!breakpoints.contains(breakpoint)) {
            breakpoints.add(breakpoint);
        }
    }

    public void removeBreakpoint(ITransformationBreakpoint breakpoint) {
        if (breakpoints.contains(breakpoint)) {
            breakpoints.remove(breakpoint);
        }
    }

    public void setDebuggerAction(DebuggerActions action) {
        this.action = action;
        actionSet = true;
    }
    
    public void setNextActivation(Activation<?> activation) {
        this.nextActivation = activation;
    }
            
    private void conflictSetChanged(TransformationDebuggerConflictSet set){
        nextActivations = Sets.newHashSet(set.getNextActivations());
        conflictingActivations = Sets.newHashSet(set.getConflictingActivations());
        for (ITransformationDebugListener listener : listeners) {
            listener.conflictSetChanged(nextActivations, conflictingActivations);
        }
    }

}
