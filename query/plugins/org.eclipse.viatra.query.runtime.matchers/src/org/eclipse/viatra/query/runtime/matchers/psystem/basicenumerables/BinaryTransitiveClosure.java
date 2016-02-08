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

package org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables;

import org.eclipse.viatra.query.runtime.matchers.psystem.IQueryReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * For a binary base pattern, computes the irreflexive transitive closure (base)+
 * 
 * @author Gabor Bergmann
 * 
 */
public class BinaryTransitiveClosure extends KeyedEnumerablePConstraint<PQuery> implements IQueryReference {

    public BinaryTransitiveClosure(PBody pBody, Tuple variablesTuple,
            PQuery pattern) {
        super(pBody, variablesTuple, pattern);
    }

    @Override
    protected String keyToString() {
        return supplierKey.getFullyQualifiedName() + "+";
    }
    
    @Override
    public PQuery getReferredQuery() {
    	return supplierKey;
    }
    
    
}
