/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameterDirection;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * This implementation calculates all valid adornments for the given query, respecting the parameter direction constraints.
 * 
 * @author Grill Balázs
 * @since 1.5
 */
public class AllValidAdornments implements IAdornmentProvider {

    @Override
    public Iterable<Set<PParameter>> getAdornments(PQuery query) {
        final Set<PParameter> ins = query.getParameters().stream().filter(PQueries.parameterDirectionPredicate(PParameterDirection.IN)).collect(Collectors.toSet()); 
        Set<PParameter> inouts = query.getParameters().stream().filter(PQueries.parameterDirectionPredicate(PParameterDirection.INOUT)).collect(Collectors.toSet()); 
        Set<Set<PParameter>> possibleInouts = Sets.powerSet(inouts);
        return Iterables.transform(possibleInouts, input -> Sets.union(ins, input));
    }

}
