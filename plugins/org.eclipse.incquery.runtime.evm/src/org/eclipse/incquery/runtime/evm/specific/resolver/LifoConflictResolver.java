/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan David, Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.resolver;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;

import com.google.common.collect.Sets;

/**
 * 
 * @author Istvan David
 *
 */
public class LifoConflictResolver implements ConflictResolver {
    @Override
    public LifoConflictSet createConflictSet() {
        return new LifoConflictSet(this);
    }

    public static final class LifoConflictSet implements ChangeableConflictSet {

        private LifoConflictResolver resolver;
        private Deque<Activation<?>> activations = new ArrayDeque<Activation<?>>();

        public LifoConflictSet(LifoConflictResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public ConflictResolver getConflictResolver() {
            return resolver;
        }

        @Override
        public Activation<?> getNextActivation() {
            return activations.peek();
        }

        @Override
        public Set<Activation<?>> getNextActivations() {
            if (activations.isEmpty()) {
                return Collections.emptySet();
            }
            HashSet<Activation<?>> activationSet = new HashSet<Activation<?>>();
            activationSet.add(getNextActivation());
            return activationSet;
        }

        @Override
        public Set<Activation<?>> getConflictingActivations() {
            return Collections.unmodifiableSet(Sets.newLinkedHashSet(activations));
        }

        @Override
        public boolean addActivation(Activation<?> activation) {
            checkArgument(activation != null, "Activation cannot be null!");
            if (activation.equals(activations.peek())) {
                return false; // no change required
            } else {
                // activation may already be in the queue, but never more than
                // once (see JavaDoc of method)
                activations.remove(activation);
                activations.push(activation);
                return true; // if the first activation changes, we consider it
                // a change in the set
            }
        }

        @Override
        public boolean removeActivation(Activation<?> activation) {
            checkArgument(activation != null, "Activation cannot be null!");
            return activations.remove(activation);
        }
    }
}