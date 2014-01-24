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

import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.IQueryReference;
import org.eclipse.incquery.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * @author Gabor Bergmann
 *
 */
public class PositivePatternCall extends KeyedEnumerablePConstraint<PQuery> implements IQueryReference {

    public PositivePatternCall(PBody pSystem, Tuple variablesTuple,
            PQuery pattern) {
        super(pSystem, variablesTuple, pattern);
    }

    @Override
    protected String keyToString() {
        return supplierKey.getFullyQualifiedName();
    }

    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	// TODO insert inferred functional dependencies here
		return super.getFunctionalDependencies();
    }

    @Override
    public PQuery getReferredQuery() {
        return supplierKey;
    }

}
