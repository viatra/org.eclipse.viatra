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

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.resolver.impl.HashSetBasedConflictSetImpl;

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
