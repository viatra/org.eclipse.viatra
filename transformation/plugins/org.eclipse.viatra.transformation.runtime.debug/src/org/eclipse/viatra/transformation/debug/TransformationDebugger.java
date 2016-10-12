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
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.communication.DebuggerTargetEndpoint;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerTargetAgent;
import org.eclipse.viatra.transformation.debug.communication.ViatraDebuggerException;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ConditionalTransformationBreakpointHandler;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.util.ActivationTraceUtil;
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
    private List<IDebuggerTargetAgent> agents = Lists.newArrayList();
    private ViatraQueryEngine engine;

    // Debug functions
    private List<ITransformationBreakpointHandler> breakpoints = Lists.newArrayList();
    private Set<Pair<RuleSpecification<?>, EventFilter<?>>> rules = Sets.newHashSet();
    private Set<Activation<?>> nextActivations = Sets.newHashSet();
    private Set<Activation<?>> conflictingActivations = Sets.newHashSet();

    private Activation<?> nextActivation;

    private DebuggerActions action = DebuggerActions.Continue;
    private boolean actionSet = false;
    private DefaultActivationCoder activationCoder = new DefaultActivationCoder();

    public TransformationDebugger(String id) {
        this.id = id;
        registerTransformationDebugListener(new DebuggerTargetEndpoint(id, this));
        
        while (!actionSet) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
        }
        
        
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
        for (ITransformationBreakpointHandler breakpoint : breakpoints) {
            if (breakpoint instanceof ConditionalTransformationBreakpointHandler && engine!=null) {
                try {
                    ((ConditionalTransformationBreakpointHandler) breakpoint).setEngine(engine);
                } catch (ViatraDebuggerException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void addedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        rules.add(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
        for (IDebuggerTargetAgent listener : agents) {
            listener.addedRule(specification, filter);
        }
    }

    @Override
    public void removedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        rules.remove(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
        for (IDebuggerTargetAgent listener : agents) {
            listener.removedRule(specification, filter);

        }
    }

    @Override
    public void afterFiring(Activation<?> activation) {
        for (IDebuggerTargetAgent listener : agents) {
            listener.activationFired(activation);
        }
    }

    @Override
    public void disposeListener() {
        List<IDebuggerTargetAgent> listenersToRemove = Lists.newArrayList();
        
        for (IDebuggerTargetAgent listener : agents) {
            try {
                listener.terminated();
                listenersToRemove.add(listener);
            } catch (ViatraDebuggerException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
            
        }
        
        for (IDebuggerTargetAgent iDebuggerTargetAgent : listenersToRemove) {
            unRegisterTransformationDebugListener(iDebuggerTargetAgent);
        }
        
        agents.clear();
        breakpoints.clear();
    }

    @Override
    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set) {
        return new TransformationDebuggerConflictSet(set);
    }

    @Override
    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
        return new TransformationDebuggerIterator(iterator);
    }

    public class TransformationDebuggerConflictSet implements ChangeableConflictSet {
        private final ChangeableConflictSet delegatedSet;

        public TransformationDebuggerConflictSet(ChangeableConflictSet delegatedSet) {
            this.delegatedSet = delegatedSet;
            conflictSetChanged(this);
        }

        @Override
        public Activation<?> getNextActivation() {
            return delegatedSet.getNextActivation();
        }

        @Override
        public Set<Activation<?>> getNextActivations() {
            return delegatedSet.getNextActivations();
        }

        @Override
        public Set<Activation<?>> getConflictingActivations() {
            return delegatedSet.getConflictingActivations();
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

            for (IDebuggerTargetAgent listener : agents) {
                listener.activationFiring(nextActivation);

            }

            if (nextActivation != null && (hasBreakpoint(nextActivation) || action == DebuggerActions.Step)) {

                for (IDebuggerTargetAgent listener : agents) {
                    listener.suspended();
                }

                while (!actionSet) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
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
        for (ITransformationBreakpointHandler breakpoint : breakpoints) {
            if (breakpoint.isEnabled() && breakpoint.shouldBreak(activation)) {
                for (IDebuggerTargetAgent listener : agents) {
                    listener.breakpointHit(breakpoint);
                }
                return true;
            }
        }
        return false;
    }

    private void registerTransformationDebugListener(IDebuggerTargetAgent listener) {
        if (!agents.contains(listener)) {
            agents.add(listener);
        }
    }

    public void disconnect() {
        disposeListener();
        breakpoints.clear();
        setDebuggerAction(DebuggerActions.Continue);
    }

    private void unRegisterTransformationDebugListener(IDebuggerTargetAgent listener) {
        if (agents.contains(listener)) {
            agents.remove(listener);
        }
    }

    private void conflictSetChanged(TransformationDebuggerConflictSet set) {
        nextActivations = Sets.newHashSet(set.getNextActivations());
        conflictingActivations = Sets.newHashSet(set.getConflictingActivations());
        for (IDebuggerTargetAgent listener : agents) {
            listener.conflictSetChanged(nextActivations, conflictingActivations);
        }
    }

    public void addBreakpoint(ITransformationBreakpointHandler breakpoint) throws ViatraDebuggerException {
        if (!breakpoints.contains(breakpoint)) {
            if (breakpoint instanceof ConditionalTransformationBreakpointHandler && engine!=null) {
                ((ConditionalTransformationBreakpointHandler) breakpoint).setEngine(engine);
            }
            breakpoints.add(breakpoint);
        }
    }

    public void disableBreakpoint(ITransformationBreakpointHandler breakpoint) {
        for (ITransformationBreakpointHandler brkp : breakpoints) {
            if(brkp.equals(breakpoint)){
                brkp.setEnabled(false);
            }
        } 
    }

    public void enableBreakpoint(ITransformationBreakpointHandler breakpoint) {
        for (ITransformationBreakpointHandler brkp : breakpoints) {
            if(brkp.equals(breakpoint)){
                brkp.setEnabled(true);
            }
        } 
    }

    public void removeBreakpoint(ITransformationBreakpointHandler breakpoint) {
        breakpoints.remove(breakpoint);
    }

    public void setDebuggerAction(DebuggerActions action) {
        this.action = action;
        actionSet = true;
    }

    public void setNextActivation(ActivationTrace trace) {
        for (Activation<?> act : conflictingActivations) {
            if (ActivationTraceUtil.compareActivationCodes(trace, activationCoder.createActivationCode(act))) {
                this.nextActivation = act;
                for (IDebuggerTargetAgent listener : agents) {
                    listener.nextActivationChanged(nextActivation);
                }
                return;
            }
        }
    }

}
