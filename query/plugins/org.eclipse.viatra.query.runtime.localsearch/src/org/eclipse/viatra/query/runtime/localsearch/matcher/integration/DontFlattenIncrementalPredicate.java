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

import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;

/**
 * This implementation forbids flattening of patterns marked to be executed with a backend other than the 
 * Local search engine (e.g. Rete). This makes is possible for the user to configure hybrid matching via using 
 * the 'search' and 'incremental keywords in the pattern definition file.
 * 
 * @since 1.5
 *
 */
public class DontFlattenIncrementalPredicate implements IFlattenCallPredicate {

    @Override
    public boolean shouldFlatten(PositivePatternCall positivePatternCall) {
        QueryEvaluationHint evaluationHints = positivePatternCall.getReferredQuery().getEvaluationHints();
        if (evaluationHints == null) return true;
        IQueryBackendFactory configuredBackend = evaluationHints.getQueryBackendFactory();
        return configuredBackend == null || configuredBackend instanceof LocalSearchBackendFactory;
    }

}
