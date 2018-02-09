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

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.IQueryReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.ITypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

/**
 * Utility class for using PQueries in functional/streaming collection operations effectively
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

    /**
     * Enumerates referred queries (without duplicates) for the given body
     */
    public static Function<PBody, Stream<PQuery>> directlyReferencedQueriesFunction() {
        return body -> (body.getConstraintsOfType(IQueryReference.class).stream().map(IQueryReference::getReferredQuery).distinct());
    }
    
    /**
     * Enumerates directly referred extensional relations (without duplicates) in the canonical form of the given query
     * @param enumerablesOnly only enumerable type constraints are considered
     * @since 2.0
     */
    public static Stream<IInputKey> directlyRequiredTypesOfQuery(PQuery query, boolean enumerablesOnly) {
        return directlyRequiredTypesOfDisjunction(query.getDisjunctBodies(), enumerablesOnly);
    }
    
    /**
     * Enumerates directly referred extensional relations (without duplicates) for the given formulation of a query.
     * @param enumerablesOnly only enumerable type constraints are considered
     * @return 
     * @since 2.0
     */
    public static Stream<IInputKey> directlyRequiredTypesOfDisjunction(PDisjunction disjunctBodies, boolean enumerablesOnly) {
        Class<? extends ITypeConstraint> filterClass = enumerablesOnly?TypeConstraint.class : ITypeConstraint.class;
        return disjunctBodies.getBodies().stream().flatMap(
                body -> body.getConstraintsOfType(filterClass).stream()).map(
                constraint -> constraint.getEquivalentJudgement().getInputKey()).distinct();
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
            body.getConstraints().forEach(traceables::add);
        });
        return traceables;
    }

}
