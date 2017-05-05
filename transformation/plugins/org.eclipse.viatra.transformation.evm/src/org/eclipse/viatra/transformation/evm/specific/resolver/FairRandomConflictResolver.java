/*******************************************************************************
 * Copyright (c) 2010-2013 Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
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
