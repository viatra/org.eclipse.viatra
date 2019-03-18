/*******************************************************************************
 * Copyright (c) 2010-2013 Gabor Bergmann, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.resolver.impl.HashSetBasedConflictSetImpl;

public class CallbackConflictResolver implements ConflictResolver {
    
    public interface ActivationChoiceStrategy {
        public Activation<?> selectNextActivation(Collection<Activation<?>> activations);
    }
    
    ActivationChoiceStrategy chooser;
    
    public CallbackConflictResolver(ActivationChoiceStrategy chooser) {
        super();
        this.chooser = chooser;
    }

    public ActivationChoiceStrategy getChooser() {
        return chooser;
    }	
    protected void setChooser(ActivationChoiceStrategy chooser) {
        this.chooser = chooser;
    }

    @Override
    public ConflictSetImpl createConflictSet() {
        return new ConflictSetImpl();
    }
    
    final class ConflictSetImpl extends HashSetBasedConflictSetImpl {
        
        @Override
        public ConflictResolver getConflictResolver() {
            return CallbackConflictResolver.this;
        }

        @Override
        public Activation<?> getNextActivation() {
            return container.isEmpty() ? 
                    null : 
                    chooser.selectNextActivation(Collections.unmodifiableSet(container));
        }
        
    }


}
