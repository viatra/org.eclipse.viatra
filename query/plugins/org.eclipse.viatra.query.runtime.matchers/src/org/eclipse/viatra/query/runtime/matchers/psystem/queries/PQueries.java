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

import org.eclipse.viatra.query.runtime.matchers.psystem.IQueryReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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
        return new Predicate<PQuery>() {

            @Override
            public boolean apply(PQuery query) {
                return query.getStatus().equals(status);
            }
        };
    }

    /**
     * Function that returns the qualified name of a query
     */
    public static Function<PQuery, String> queryNameFunction() {
        return new Function<PQuery, String>() {

            @Override
            public String apply(PQuery query) {
                return query.getFullyQualifiedName();
            }
        };
    }

    public static Function<PParameter, String> parameterNameFunction() {
        return new Function<PParameter, String>() {

            @Override
            public String apply(PParameter param) {
                return param.getName();
            }
        };
    }

    public static Function<IQueryReference, PQuery> queryOfReferenceFunction() {
        return new Function<IQueryReference, PQuery>() {

            @Override
            public PQuery apply(IQueryReference reference) {
                return reference.getReferredQuery();
            }
        };
    }

    public static Function<PBody, Iterable<PQuery>> directlyReferencedQueriesFunction() {
        return new Function<PBody, Iterable<PQuery>>() {

            @Override
            public Iterable<PQuery> apply(PBody body) {
                return Iterables.transform(body.getConstraintsOfType(IQueryReference.class), PQueries.queryOfReferenceFunction());
            }
        };
    }
    
    /**
     * @since 1.4
     */
    public static Predicate<PParameter> parameterDirectionPredicate(final PParameterDirection direction){
        return new Predicate<PParameter>() {

            @Override
            public boolean apply(PParameter input) {
                return input.getDirection() == direction;
            }
        };
    }
}
