/*******************************************************************************
 * Copyright (c) 2014-2016 Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests;

import java.util.Collections;

import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchGenericBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.rete.matcher.DRedReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.TimelyReteBackendFactory;

public enum BackendType {
    Rete, Rete_DRed, Rete_Timely_First_Only_Sequential, Rete_Timely_First_Only_Parallel, Rete_Timely_Faithful_Sequential, 
    Rete_Timely_Faithful_Parallel, LocalSearch, LocalSearch_Generic, LocalSearch_Flat, LocalSearch_NoBase;

    public IQueryBackendFactory getNewBackendInstance() {
        switch (this) {
        case Rete:
            return ReteBackendFactory.INSTANCE;
        case Rete_DRed:
            return DRedReteBackendFactory.INSTANCE;
        case Rete_Timely_First_Only_Sequential:
            return TimelyReteBackendFactory.FIRST_ONLY_SEQUENTIAL;
        case Rete_Timely_First_Only_Parallel:
            return TimelyReteBackendFactory.FIRST_ONLY_PARALLEL;
        case Rete_Timely_Faithful_Sequential:
            return TimelyReteBackendFactory.FAITHFUL_SEQUENTIAL;
        case Rete_Timely_Faithful_Parallel:
            return TimelyReteBackendFactory.FAITHFUL_PARALLEL;
        case LocalSearch_Flat:
        case LocalSearch_NoBase:
        case LocalSearch:
            return LocalSearchEMFBackendFactory.INSTANCE;
        case LocalSearch_Generic:
            return LocalSearchGenericBackendFactory.INSTANCE;
        default:
            return null;
        }
    }

    public QueryEvaluationHint getHints() {
        switch (this) {
        case LocalSearch:
            return LocalSearchHints.getDefault().build();
        case LocalSearch_Flat:
            return LocalSearchHints.getDefaultFlatten().build();
        case LocalSearch_NoBase:
            return LocalSearchHints.getDefaultNoBase().build();
        case LocalSearch_Generic:
            return LocalSearchHints.getDefaultGeneric().build();
        default:
            return new QueryEvaluationHint(Collections.<QueryHintOption<?>, Object> emptyMap(),
                    getNewBackendInstance());
        }
    }
}
