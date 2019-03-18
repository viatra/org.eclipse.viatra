/*******************************************************************************
 * Copyright (c) 2010-2013, Peter Lunk, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
