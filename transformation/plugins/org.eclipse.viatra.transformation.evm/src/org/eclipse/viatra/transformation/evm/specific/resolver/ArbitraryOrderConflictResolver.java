/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.resolver.impl.HashSetBasedConflictSetImpl;

/**
 * @author Abel Hegedus
 *
 */
public class ArbitraryOrderConflictResolver implements ConflictResolver {

    @Override
    public ArbitraryConflictSet createConflictSet() {
        return new ArbitraryConflictSet();
    }
    
    public final class ArbitraryConflictSet extends HashSetBasedConflictSetImpl  {

        @Override
        public Activation<?> getNextActivation() {
            if(!container.isEmpty()) {
                return container.iterator().next();
            }
            return null;
        }

        @Override
        public ArbitraryOrderConflictResolver getConflictResolver() {
            return ArbitraryOrderConflictResolver.this;
        }

        
    }
}
