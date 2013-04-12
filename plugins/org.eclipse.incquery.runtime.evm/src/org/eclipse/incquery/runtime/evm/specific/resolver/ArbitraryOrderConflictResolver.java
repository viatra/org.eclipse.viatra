/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.resolver;

import java.util.Collections;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ConflictResolver;
import org.eclipse.incquery.runtime.evm.api.ConflictSet;
import org.eclipse.incquery.runtime.evm.specific.resolver.ArbitraryOrderConflictResolver.ArbitraryConflictSet;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class ArbitraryOrderConflictResolver implements ConflictResolver<ArbitraryConflictSet> {

    @Override
    public ArbitraryConflictSet createConflictSet() {
        return new ArbitraryConflictSet(this);
    }
    
    public final class ArbitraryConflictSet implements ConflictSet {

        private final Set<Activation<?>> container;
        private final ArbitraryOrderConflictResolver resolver;
        /**
         * 
         */
        protected ArbitraryConflictSet(ArbitraryOrderConflictResolver resolver) {
            this.resolver = resolver;
            container = Sets.newHashSet();
        }
        
        @Override
        public Activation<?> getNextActivation() {
            if(!container.isEmpty()) {
                return container.iterator().next();
            }
            return null;
        }

        @Override
        public boolean addActivation(Activation<?> activation) {
            return container.add(activation);
        }

        @Override
        public boolean removeActivation(Activation<?> activation) {
            return container.remove(activation);
        }

        @Override
        public ArbitraryOrderConflictResolver getConflictResolver() {
            return resolver;
        }

        @Override
        public Set<Activation<?>> getNextActivations() {
            return Collections.unmodifiableSet(container);
        }

        @Override
        public Set<Activation<?>> getConflictingActivations() {
            return Collections.unmodifiableSet(container);
        }
        
    }
}
