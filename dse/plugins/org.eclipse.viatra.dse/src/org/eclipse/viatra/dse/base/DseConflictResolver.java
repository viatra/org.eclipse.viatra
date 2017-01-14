/*******************************************************************************
 * Copyright (c) 2010-2017, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;

public class DseConflictResolver implements ConflictResolver {

    private ConflictResolver activationOrderingconflictResolver;
    private IStateCoder stateCoder;
    private DseConflictSet lastCreatedConflictSet;

    public DseConflictResolver(ConflictResolver activationOrderingConflictResolver, IStateCoder stateCoder) {
        this.activationOrderingconflictResolver = activationOrderingConflictResolver;
        this.stateCoder = stateCoder;
    }

    @Override
    public ChangeableConflictSet createConflictSet() {
        lastCreatedConflictSet = new DseConflictSet(this, activationOrderingconflictResolver, stateCoder);
        return lastCreatedConflictSet;
    }

    public DseConflictSet getLastCreatedConflictSet() {
        return lastCreatedConflictSet;
    }
}
