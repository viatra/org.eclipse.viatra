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

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class MatcherReference {
    final PQuery query;
    final Set<PParameter> adornment;
    // XXX In case of older (pre-1.4) VIATRA versions, PParameters were not stable, see bug 498348
    final Set<String> boundParameterNames;
        
    public MatcherReference(PQuery query, Set<PParameter> adornment) {
        super();
        this.query = query;
        this.adornment = adornment;
        this.boundParameterNames = Sets.newHashSet(Iterables.transform(adornment, PQueries.parameterNameFunction()));
    }
    public PQuery getQuery() {
        return query;
    }
    public Set<PParameter> getAdornment() {
        return adornment;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + ((boundParameterNames == null) ? 0 : boundParameterNames.hashCode());
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
        if (boundParameterNames == null) {
            if (other.boundParameterNames != null)
                return false;
        } else if (!boundParameterNames.equals(other.boundParameterNames))
            return false;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        return true;
    }
    
    
}