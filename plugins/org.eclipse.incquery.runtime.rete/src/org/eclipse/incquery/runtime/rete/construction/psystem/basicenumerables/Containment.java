/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.psystem.basicenumerables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;

/**
 * @author Bergmann Gábor
 * 
 */
public class Containment<PatternDescription, StubHandle> extends CoreModelRelationship<PatternDescription, StubHandle> {

	PVariable parent; 
	PVariable child;
    /**
     * @param variablesTuple
     */
    public Containment(PSystem<PatternDescription, StubHandle, ?> pSystem, PVariable parent, PVariable child,
            boolean transitive) {
        super(pSystem, parent, child, transitive);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @return
     */
    @Override
    protected Stub<StubHandle> doCreateDirectStub() {
        return buildable.containmentDirectStub(variablesTuple);
    }

    /**
     * @return
     */
    @Override
    protected Stub<StubHandle> doCreateTransitiveStub() {
        return buildable.containmentTransitiveStub(variablesTuple);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.rete.construction.psystem.BasePConstraint#getFunctionalKeys()
     */
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	final HashMap<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	result.put(Collections.singleton(child), Collections.singleton(parent));
		return result;
    }

}
