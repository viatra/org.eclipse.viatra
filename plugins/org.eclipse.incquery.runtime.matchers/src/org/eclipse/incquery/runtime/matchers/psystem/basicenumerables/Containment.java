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

package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

/**
 * @author Gabor Bergmann
 * 
 */
public class Containment extends CoreModelRelationship {


	public Containment(PBody pSystem, PVariable parent, PVariable child,
            boolean transitive) {
        super(pSystem, parent, child, transitive);
    }
    
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	PVariable parent = getVariableInTuple(0);
		PVariable child = getVariableInTuple(1);
    	final Map<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	result.put(Collections.singleton(child), Collections.singleton(parent));
		return result;
    }

}
