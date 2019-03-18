/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import java.util.Objects;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

/**
 * @author Abel Hegedus
 *
 */
public class ViatraQueryMatchEventFilter<Match extends IPatternMatch> implements EventFilter<Match> {

    private Match filterMatch;
    
    public Match getFilterMatch() {
        return filterMatch;
    }

    @Override
    public boolean isProcessable(Match eventAtom) {
        if(filterMatch == null) {
            return true;
        }
        return filterMatch.isCompatibleWith(eventAtom);
    }

    protected ViatraQueryMatchEventFilter(Match filterMatch) {
        Preconditions.checkArgument(filterMatch != null, "Cannot create filter with null match");
        this.filterMatch = filterMatch;
    }
    
    /**
     * Only used internally to create empty filters
     */
    protected ViatraQueryMatchEventFilter() {}
    
    public static <Match extends IPatternMatch> ViatraQueryMatchEventFilter<Match> createFilter(Match eventAtom) {
        Preconditions.checkArgument(eventAtom != null, "Cannot create filter for null match, use createEmptyFilter() instead!");
        Preconditions.checkArgument(!eventAtom.isMutable(), "Cannot create filter for mutable match!");
        return new ViatraQueryMatchEventFilter<Match>(eventAtom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterMatch);
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
        ViatraQueryMatchEventFilter other = (ViatraQueryMatchEventFilter) obj;
        return Objects.equals(filterMatch, other.filterMatch);
    }
    
}
