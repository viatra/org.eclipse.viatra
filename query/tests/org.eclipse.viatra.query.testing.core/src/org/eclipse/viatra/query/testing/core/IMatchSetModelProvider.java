/** 
 * Copyright (c) 2010-2015, Grill Balazs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balazs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord;

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
     * @throws ViatraQueryException
     * @since 1.5.2 
     */
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(EMFScope scope,
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter)
                    throws ViatraQueryException;
    
    /**
     * Creates a snapshot of the current matches of the given query specification
     * 
     * @param querySpecification
     * @return recorded matches
     * @throws ViatraQueryException
     * 
     */
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet rs,
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter)
                    throws ViatraQueryException;

    
    /**
     * Return true if the result of this provider is updated by input model modifications, thus a subsequent
     * getMathcSetRecord() call will reflect the changes.
     * 
     * @return
     */
    public boolean updatedByModify();
    
    
    public void dispose();
}
