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
package org.eclipse.viatra.transformation.evm.specific.resolver;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;

/**
 * @author Abel Hegedus
 *
 */
public abstract class ReconfigurableConflictResolver<RCSet extends ChangeableConflictSet> implements ConflictResolver {

    private Set<WeakReference<RCSet>> conflictSets = new HashSet<WeakReference<RCSet>>(); 
    
    @Override
    public RCSet createConflictSet() {
        RCSet conflictSet = createReconfigurableConflictSet();
        conflictSets.add(new WeakReference<RCSet>(conflictSet));
        return conflictSet;
    }

    protected abstract RCSet createReconfigurableConflictSet();
    
    /**
     * @return the conflictSets
     */
    protected Set<WeakReference<RCSet>> getConflictSets() {
        return conflictSets;
    }
}
