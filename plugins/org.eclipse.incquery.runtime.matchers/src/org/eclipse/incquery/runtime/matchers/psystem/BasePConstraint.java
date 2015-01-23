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

package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class BasePConstraint implements PConstraint {
	
	
    protected PBody pSystem;
    private final Set<PVariable> affectedVariables;
    
    
    private final int sequentialID = nextID++;

	private static int nextID = 0;

    public BasePConstraint(PBody pSystem, Set<PVariable> affectedVariables) {
        super();
        this.pSystem = pSystem;
        this.affectedVariables = new HashSet<PVariable>(affectedVariables);

        for (PVariable pVariable : affectedVariables) {
            pVariable.refer(this);
        }
        pSystem.registerConstraint(this);
    }

    @Override
    public String toString() {
        return "PC[" + getClass().getSimpleName() + ":" + toStringRest() + "]";
    }

    protected abstract String toStringRest();

    @Override
    public Set<PVariable> getAffectedVariables() {
        return affectedVariables;
    }
    
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	return Collections.emptyMap();
    }

    @Override
    public void replaceVariable(PVariable obsolete, PVariable replacement) {
        pSystem.checkMutability();
        if (affectedVariables.remove(obsolete)) {
            affectedVariables.add(replacement);
            obsolete.unrefer(this);
            replacement.refer(this);
            doReplaceVariable(obsolete, replacement);
        }
    }

    protected abstract void doReplaceVariable(PVariable obsolete, PVariable replacement);

    @Override
    public void delete() {
        pSystem.checkMutability();
        for (PVariable pVariable : affectedVariables) {
            pVariable.unrefer(this);
        }
        pSystem.unregisterConstraint(this);
    }

    @Override
    public void checkSanity() throws QueryProcessingException {
    }

    public PBody getPSystem() {
        return pSystem;
    }
    
    @Override
	public int getMonotonousID() {
		return sequentialID;
	}    
}
