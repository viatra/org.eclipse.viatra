/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Istvan Rath  - implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

import com.google.common.base.Objects;

/**
 * Multi pattern match event filter.
 * @author Istvan Rath
 *
 */
public class IncQueryMultiPatternMatchEventFilter<Match extends IPatternMatch> implements EventFilter<Match> {

    private Collection<Match> filterMatches;
    
    private IncQueryFilterSemantics semantics;
    
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

    protected IncQueryMultiPatternMatchEventFilter(Collection<Match> filterMatches, IncQueryFilterSemantics semantics) {
        checkArgument(filterMatches != null, "Cannot create filter with null matches");
        this.semantics = semantics;
        this.filterMatches = filterMatches;
    }
    
    public static <Match extends IPatternMatch> IncQueryMultiPatternMatchEventFilter<Match> createFilter(Collection<Match> eventAtoms, IncQueryFilterSemantics semantics) {
        checkArgument(eventAtoms != null, "Cannot create filter for null match, use createEmptyFilter() instead!");
        for (Match eventAtom : eventAtoms) {
            checkArgument(!eventAtom.isMutable(), "Cannot create filter for mutable match!");
        }
        return new IncQueryMultiPatternMatchEventFilter<Match>(eventAtoms, semantics);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(filterMatches);
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
        IncQueryMultiPatternMatchEventFilter other = (IncQueryMultiPatternMatchEventFilter) obj;
        return Objects.equal(filterMatches, other.filterMatches);
    }
    
}
