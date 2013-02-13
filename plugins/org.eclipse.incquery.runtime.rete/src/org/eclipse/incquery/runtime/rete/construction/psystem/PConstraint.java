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

package org.eclipse.incquery.runtime.rete.construction.psystem;

import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;

/**
 * @author Bergmann Gábor
 * 
 */
public interface PConstraint {

	/**
	 * All variables affected by this constraint.
	 */
    public Set<PVariable> getAffectedVariables();

	/**
	 * The set of variables whose potential values can be enumerated (once all non-deduced variables have known values).  
	 */
    public Set<PVariable> getDeducedVariables();
    
    /**
     * Determinants are sets of variables that are guaranteed to functionally determine the value of the rest of variables.
     * Keys are irreducible determinants. 
     * The set of all affected variables is always a determinant, therefore it need not be returned even if it is a key.
     * @return non-trivial keys.   
     */
    public Set<Set<PVariable>> getFunctionalKeys();  

    public void replaceVariable(PVariable obsolete, PVariable replacement);

    public void delete();

    public void checkSanity() throws RetePatternBuildException;
}
