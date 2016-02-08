/** 
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.incquery.testing.core;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord;

/**
 * Implementations of this interface can provide match set snapshots for a given {@link IQuerySpecification}. The query
 * scope must be defined by the implementer (e.g. an EMF notifier or a pre-executed snapshot)
 *
 * @param <Match>
 */
public interface IMatchSetModelProvider {

    /**
     * Creates a snapshot of the current matches of the given query specification
     * 
     * @param querySpecification
     * @return recorded matches
     * @throws IncQueryException
     */
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet resourceSet,
            IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, Match filter)
                    throws IncQueryException;

    public void dispose();

}
