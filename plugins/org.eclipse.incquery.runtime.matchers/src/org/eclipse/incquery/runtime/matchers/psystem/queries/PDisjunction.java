/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.queries;

import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PBody;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * A disjunction is a set of bodies representing separate conditions. A {@link PQuery} has a single, canonical
 * PDisjunction, that can be replaced using rewriter
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class PDisjunction {

    private ImmutableSet<PBody> bodies;
    private PQuery query;

    public PDisjunction(Set<PBody> bodies) {
        this(null, bodies);
    }

    public PDisjunction(PQuery query, Set<PBody> bodies) {
        super();
        this.query = query;
        final Builder<PBody> builder = ImmutableSet.builder();
        for (PBody body : bodies) {
            body.setContainerDisjunction(this);
            builder.add(body);
        }
        this.bodies = builder.build();
    }

    /**
     * Returns an immutable set of bodies that consists of this disjunction
     * 
     * @return the bodies
     */
    public Set<PBody> getBodies() {
        return bodies;
    }

    /**
     * Returns the corresponding query specification. May be null if not set.
     */
    public PQuery getQuery() {
        return query;
    }

    /**
     * Decides whether a disjunction is mutable. A disjunction is mutable if all its contained bodies are mutable.
     * 
     */
    public boolean isMutable() {
        for (PBody body : bodies) {
            if (!body.isMutable()) {
                return false;
            }
        }
        return true;
    }
}
