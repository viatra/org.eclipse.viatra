/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Table;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 * @since 1.7
 *
 */
public enum LocalSearchGenericBackendFactory implements IQueryBackendFactory {
    
    INSTANCE;
    
    /**
     * @since 1.5
     */
    @Override
    public IQueryBackend create(IQueryBackendContext context) {
        return new LocalSearchBackend(context) {
            
            @Override
            protected AbstractLocalSearchResultProvider initializeResultProvider(PQuery query, QueryEvaluationHint hints) throws QueryProcessingException {
                return new GenericLocalSearchResultProvider(this, context, query, planProvider, hints);
            }

            @Override
            public Table<EDataType, EClass, Set<EAttribute>> geteAttributesByTypeForEClass() {
                throw new UnsupportedOperationException("geteAttributesByTypeForEClass() is a legacy call not supported by the generic LS backend.");
            }
            
            
        };
    }
    
    @Override
    public Class<? extends IQueryBackend> getBackendClass() {
        return LocalSearchBackend.class;
    }

    /**
     * @since 1.4
     */
    @Override
    public IMatcherCapability calculateRequiredCapability(PQuery query, QueryEvaluationHint hint) {
        return LocalSearchHints.parse(hint);
    }

}
