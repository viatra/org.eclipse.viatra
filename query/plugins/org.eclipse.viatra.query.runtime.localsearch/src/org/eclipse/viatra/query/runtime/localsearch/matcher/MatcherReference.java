/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Marton Bur, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher;

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

public class MatcherReference {
    final PQuery query;
    final Set<Integer> adornment;
    
    public MatcherReference(PQuery query, Set<Integer> adornment) {
        super();
        this.query = query;
        this.adornment = adornment;
    }
    public PQuery getQuery() {
        return query;
    }
    public Set<Integer> getAdornment() {
        return adornment;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adornment == null) ? 0 : adornment.hashCode());
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MatcherReference other = (MatcherReference) obj;
        if (adornment == null) {
            if (other.adornment != null)
                return false;
        } else if (!adornment.equals(other.adornment))
            return false;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        return true;
    }
    
    
}