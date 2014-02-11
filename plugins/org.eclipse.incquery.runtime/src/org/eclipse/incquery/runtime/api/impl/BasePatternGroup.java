/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IQueryGroup;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.internal.apiimpl.IncQueryEngineImpl;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.psystem.PQueries;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

/**
 * Base implementation of {@link IQueryGroup}.
 *
 * @author Mark Czotter
 *
 */
public abstract class BasePatternGroup implements IQueryGroup {

    @Override
    public void prepare(Notifier emfRoot) throws IncQueryException {
        prepare(IncQueryEngine.on(emfRoot));
    }

    @Override
    public void prepare(IncQueryEngine engine) throws IncQueryException {
        try {
            final Set<PQuery> patterns = new HashSet<PQuery>(getSpecifications());
            Collection<String> uninitializedPatterns = Collections2.transform(
                    Collections2.filter(patterns, PQueries.queryStatusPredicate(PQueryStatus.UNINITIALIZED)),
                    PQueries.queryNameFunction());
            Preconditions.checkState(uninitializedPatterns.isEmpty(), "Uninitialized query(s) found: %s", Joiner.on(", ")
                    .join(uninitializedPatterns));
            Collection<String> erroneousPatterns = Collections2.transform(
                    Collections2.filter(patterns, PQueries.queryStatusPredicate(PQueryStatus.ERROR)),
                    PQueries.queryNameFunction());
            Preconditions.checkState(erroneousPatterns.isEmpty(), "Erroneous query(s) found: %s", Joiner.on(", ")
                    .join(erroneousPatterns));
            final IncQueryEngineImpl engineImpl = (IncQueryEngineImpl) engine;
            engineImpl.getReteEngine().buildMatchersCoalesced(patterns);
        } catch (QueryPlannerException e) {
            throw new IncQueryException(e);
        }
    }

}
