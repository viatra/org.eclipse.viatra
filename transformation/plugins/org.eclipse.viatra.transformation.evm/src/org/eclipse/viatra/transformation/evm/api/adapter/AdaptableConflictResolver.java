/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictSet;

/**
 * Adaptable {@link ConflictResolver} implementation that enables an {@link IEVMAdapter} implementation to replace the
 * current {@link ConflictSet} with a modified one.
 * 
 * @author Peter Lunk
 *
 */
public class AdaptableConflictResolver implements ConflictResolver {
    protected final ConflictResolver delegatedConflictResolver;
    protected final AdaptableEVM container;

    public AdaptableConflictResolver(ConflictResolver delegatedConflictResolver, AdaptableEVM container) {
        this.delegatedConflictResolver = delegatedConflictResolver;
        Preconditions.checkState(this.delegatedConflictResolver != null, "Delegated Conflict resolver must be set.");
        this.container = container;
    }

    @Override
    public ChangeableConflictSet createConflictSet() {
        return container.getConflictSet(delegatedConflictResolver.createConflictSet());
    }
}
