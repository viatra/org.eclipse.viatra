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
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.viatra.query.runtime.matchers.psystem.IQueryReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

/**
 * Utility class for using PQueries in Guava collection operations effectively
 *
 * @author Zoltan Ujhelyi
 *
 */
public final class PQueries {

    /**
     * Hidden constructor for utility class
     */
    private PQueries(){}

    /**
     * Predicate checking for the status of selected queries
     *
     */
    public static Predicate<PQuery> queryStatusPredicate(final PQueryStatus status) {
        return query -> query.getStatus().equals(status);
    }

    public static Function<PBody, Stream<PQuery>> directlyReferencedQueriesFunction() {
        return body -> (body.getConstraintsOfType(IQueryReference.class).stream().map(IQueryReference::getReferredQuery));
    }
    
    /**
     * @since 1.4
     */
    public static Predicate<PParameter> parameterDirectionPredicate(final PParameterDirection direction){
        return input -> input.getDirection() == direction;
    }

    /**
     * Returns all {@link PTraceable}s contained in the given {@link PQuery}: itself, its bodies and their constraints.
     * 
     * @since 1.6
     */
    public static Set<PTraceable> getTraceables(PQuery query) {
        final Set<PTraceable> traceables = new HashSet<>();
        traceables.add(query);
        query.getDisjunctBodies().getBodies().forEach(body -> {
            traceables.add(body);
            body.getConstraints().forEach(c -> traceables.add(c));
        });
        return traceables;
    }

}
