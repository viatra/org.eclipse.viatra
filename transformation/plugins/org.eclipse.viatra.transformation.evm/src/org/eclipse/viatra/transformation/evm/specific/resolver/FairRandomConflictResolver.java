/*******************************************************************************
 * Copyright (c) 2010-2013 Gabor Bergmann, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver;


import java.util.Random;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.resolver.impl.RandomAccessConflictSetImpl;

public class FairRandomConflictResolver implements ConflictResolver {
    
    @Override
    public ConflictSetImpl createConflictSet() {
        return new ConflictSetImpl();
    }
    
    final class ConflictSetImpl extends RandomAccessConflictSetImpl {
        Random rnd = new Random();
        
        @Override
        public ConflictResolver getConflictResolver() {
            return FairRandomConflictResolver.this;
        }

        @Override
        public Activation<?> getNextActivation() {
            if (activationList.isEmpty()) return null;
            int index = rnd.nextInt(activationList.size());
            return activationList.get(index);
        }
        
    }
}
