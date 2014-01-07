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

import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class CoreModelRelationship extends EnumerablePConstraint {

    protected boolean transitive;

    public CoreModelRelationship(PBody pSystem, PVariable parent, PVariable child,
            boolean transitive) {
        super(pSystem, new FlatTuple(parent, child));
        this.transitive = transitive;
    }

    @Override
    protected String toStringRestRest() {
        return transitive ? "transitive" : "direct";
    }

    public boolean isTransitive() {
        return transitive;
    }

}