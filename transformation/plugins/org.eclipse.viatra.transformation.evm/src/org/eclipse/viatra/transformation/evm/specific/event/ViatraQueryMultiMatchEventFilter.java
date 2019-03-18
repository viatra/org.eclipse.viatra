/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import java.util.Collection;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

/**
 * Multi pattern match event filter.
 * @author Istvan Rath
 *
 */
public class ViatraQueryMultiMatchEventFilter<Match extends IPatternMatch> implements EventFilter<Match> {

    private Collection<Match> filterMatches;
    
    private ViatraQueryFilterSemantics semantics;
    
    public Collection<Match> getFilterMatches() {
        return filterMatches;
    }

    @Override
    public boolean isProcessable(Match eventAtom) {
        if(filterMatches == null) {
            return true;
        }
        switch (semantics){
        default: case OR:
            for (Match filterMatch : filterMatches) {
                if (filterMatch.isCompatibleWith(eventAtom)) {
                    return true;
                }
            }
            return false;
        case AND:
            for (Match filterMatch : filterMatches) {
                if (!filterMatch.isCompatibleWith(eventAtom)) {
                    return false;
                }
            }
            return true;
        }
        
    }

    protected ViatraQueryMultiMatchEventFilter(Collection<Match> filterMatches, ViatraQueryFilterSemantics semantics) {
        Preconditions.checkArgument(filterMatches != null, "Cannot create filter with null matches");
        this.semantics = semantics;
        this.filterMatches = filterMatches;
    }
    
    public static <Match extends IPatternMatch> ViatraQueryMultiMatchEventFilter<Match> createFilter(Collection<Match> eventAtoms, ViatraQueryFilterSemantics semantics) {
        Preconditions.checkArgument(eventAtoms != null, "Cannot create filter for null match, use createEmptyFilter() instead!");
        for (Match eventAtom : eventAtoms) {
            Preconditions.checkArgument(!eventAtom.isMutable(), "Cannot create filter for mutable match!");
        }
        return new ViatraQueryMultiMatchEventFilter<Match>(eventAtoms, semantics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterMatches);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        ViatraQueryMultiMatchEventFilter other = (ViatraQueryMultiMatchEventFilter) obj;
        return Objects.equals(filterMatches, other.filterMatches);
    }
    
}
