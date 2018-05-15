/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests;

import java.util.Collections;

import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchGenericBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;

public enum BackendType {
    Rete, LocalSearch,
    LocalSearch_Generic,
    LocalSearch_Flat,
    LocalSearch_NoBase;
    
    public IQueryBackendFactory getNewBackendInstance() {
        switch(this) {
            case Rete: return ReteBackendFactory.INSTANCE;
            case LocalSearch_Flat:
            case LocalSearch_NoBase:
            case LocalSearch: 
                return LocalSearchEMFBackendFactory.INSTANCE;
            case LocalSearch_Generic:
                return LocalSearchGenericBackendFactory.INSTANCE;
            default: return null;
        }
    }
    
    public QueryEvaluationHint getHints(){
        switch(this){
        case LocalSearch:
            return LocalSearchHints.getDefault().build();
        case LocalSearch_Flat:
            return LocalSearchHints.getDefaultFlatten().build();
        case LocalSearch_NoBase:
            return LocalSearchHints.getDefaultNoBase().build();
        case LocalSearch_Generic:
            return LocalSearchHints.getDefaultGeneric().build();
        default:
            return new QueryEvaluationHint(Collections.<QueryHintOption<?>, Object>emptyMap(), getNewBackendInstance());
        }
    }
}
