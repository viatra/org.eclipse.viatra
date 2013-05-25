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

import java.util.Map;
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
     * A (preferably minimal) cover of known functional dependencies between variables.
     * @return non-trivial functional dependencies in the form of {variables} --> {variables}, where dependencies with the same lhs are unified.   
     */
    public Map<Set<PVariable>,Set<PVariable>> getFunctionalDependencies();  

    public void replaceVariable(PVariable obsolete, PVariable replacement);

    public void delete();

    public void checkSanity() throws RetePatternBuildException;
}
