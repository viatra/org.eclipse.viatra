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
package org.eclipse.incquery.runtime.matchers.psystem;

import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Utility class for using PQueries in Guava collection operations effectively
 *
 * @author Zoltan Ujhelyi
 *
 */
public class PQueries {

    /**
     * Hidden constructor for utility class
     */
    private PQueries(){};

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
     * @return
     */
    public static Function<PQuery, String> queryNameFunction() {
        return new Function<PQuery, String>() {

            @Override
            public String apply(PQuery query) {
                return query.getFullyQualifiedName();
            }
        };
    }
}
