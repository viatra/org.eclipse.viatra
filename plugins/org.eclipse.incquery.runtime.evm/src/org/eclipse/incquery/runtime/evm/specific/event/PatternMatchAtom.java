/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.event;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.event.Atom;

import com.google.common.base.Objects;

/**
 * @author Abel Hegedus
 *
 */
public class PatternMatchAtom<Match extends IPatternMatch> implements Atom {

    private final Match match;

    /**
     * 
     */
    public PatternMatchAtom(Match match) {
        this.match = match;
    }
    
    @Override
    public boolean isCompatibleWith(Atom other) {
        if(this == other) {
            return true;
        }
        if(other instanceof PatternMatchAtom<?>) {
            IPatternMatch otherMatch = ((PatternMatchAtom<?>) other).getMatch();
            match.isCompatibleWith(otherMatch);
        }
        return false;
    }

    @Override
    public boolean isMutable() {
        return match.isMutable();
    }

    public Match getMatch() {
        return match;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PatternMatchAtom<?> other = (PatternMatchAtom<?>) obj;
        if (match == null) {
            if (other.match != null)
                return false;
        } else if (!match.equals(other.match))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(match);
    }

    @Override
    public boolean isEmpty() {
        if (match != null) {
            for (Object o : match.toArray()) {
                if (o != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper("Match").add("Params",match.prettyPrint()).toString();
    }

}