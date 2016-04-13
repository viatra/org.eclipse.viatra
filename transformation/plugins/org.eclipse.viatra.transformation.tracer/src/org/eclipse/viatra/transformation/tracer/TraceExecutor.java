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
package org.eclipse.viatra.transformation.tracer;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.activationcoder.IActivationCoder;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.DefaultTraceModelSerializer;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.ITraceModelSerializer;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.TransformationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.util.ActivationTraceUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.ConflictSetIterator;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractEVMAdapter;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;

import com.google.common.collect.Sets;

/**
 * Adapter implementation that loads transformation traces and executes the transformation according to them
 *
 * @author Peter Lunk
 *
 */
public class TraceExecutor extends AbstractEVMAdapter {
    TransformationTrace trace;
    IActivationCoder activationCoder;
    ITraceModelSerializer serializer;
    Iterator<?> traceIterator;

    public TraceExecutor(IActivationCoder activationCoder, ITraceModelSerializer serializer) {
        this.activationCoder = activationCoder;
        this.serializer = serializer;
        trace = serializer.loadTraceModel();
    }

    public TraceExecutor(IActivationCoder activationCoder, URI location) {
        this.activationCoder = activationCoder;
        this.serializer = new DefaultTraceModelSerializer(location);
        trace = serializer.loadTraceModel();
    }

    public TraceExecutor(URI location) {
        this.activationCoder = new DefaultActivationCoder();
        this.serializer = new DefaultTraceModelSerializer(location);
        trace = serializer.loadTraceModel();
    }

    @Override
    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set) {
        return new TraceExecutorConflictSet(set);
    }

    @Override
    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
        if (iterator instanceof ConflictSetIterator) {
            return iterator;
        } else {
            return new TraceExecutorIterator(iterator);
        }
    }

    private ActivationTrace getNextActivationCode() {
        if (traceIterator == null) {
            traceIterator = trace.getActivationTraces().iterator();
        }
        return (ActivationTrace) traceIterator.next();
    }

    

    private Activation<?> getActivation(Set<Activation<?>> activations) {
        ActivationTrace activationCode = getNextActivationCode();
        Activation<?> temp = null;
        for (Activation<?> act : activations) {
            RuleSpecification<?> specification = act.getInstance().getSpecification();
            if (specification.getName().equals(activationCode.getRuleName())) {
                if (ActivationTraceUtil.compareActivationCodes(activationCoder.createActivationCode(act), activationCode)) {
                    temp = act;
                }
            }
        }
        if (temp != null) {
            activations.remove(temp);
            return temp;
        } else {
            throw new IllegalStateException("No Activation found for trace:" + activationCode.toString());
        }
    }

    public class TraceExecutorConflictSet implements ChangeableConflictSet {
        private final ChangeableConflictSet delegatedConflictSet;

        public TraceExecutorConflictSet(ChangeableConflictSet delegatedConflictSet) {
            this.delegatedConflictSet = delegatedConflictSet;
        }

        @Override
        public Activation<?> getNextActivation() {
            Set<Activation<?>> nextActivations = Sets.newHashSet(delegatedConflictSet.getConflictingActivations());
            if (nextActivations.size() > 0) {
                return getActivation(nextActivations);
            } else {
                return null;
            }
        }

        @Override
        public Set<Activation<?>> getNextActivations() {
            return delegatedConflictSet.getNextActivations();
        }

        @Override
        public Set<Activation<?>> getConflictingActivations() {
            return delegatedConflictSet.getConflictingActivations();
        }

        @Override
        public ConflictResolver getConflictResolver() {
            return delegatedConflictSet.getConflictResolver();
        }

        @Override
        public boolean addActivation(Activation<?> activation) {
            return delegatedConflictSet.addActivation(activation);
        }

        @Override
        public boolean removeActivation(Activation<?> activation) {
            return delegatedConflictSet.removeActivation(activation);
        }

    }

    public class TraceExecutorIterator implements Iterator<Activation<?>> {
        private final Set<Activation<?>> activations = Sets.newHashSet();

        public TraceExecutorIterator(Iterator<Activation<?>> delegatedIterator) {
            while (delegatedIterator.hasNext()) {
                activations.add(delegatedIterator.next());
            }
        }

        @Override
        public boolean hasNext() {
            return !activations.isEmpty();
        }

        @Override
        public Activation<?> next() {
            return getActivation(activations);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Deletion from this iterator is not supported.");

        }

    }
}
