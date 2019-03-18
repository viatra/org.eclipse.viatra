/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables;

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.IQueryReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * @since 2.0
 */
public abstract class AbstractTransitiveClosure extends KeyedEnumerablePConstraint<PQuery> implements IQueryReference, ITypeInfoProviderConstraint {

    public AbstractTransitiveClosure(PBody pBody, Tuple variablesTuple, PQuery supplierKey) {
        super(pBody, variablesTuple, supplierKey);
    }

    @Override
    public PQuery getReferredQuery() {
        return supplierKey;
    }

    /**
     * @since 1.3
     */
    @Override
    public Set<TypeJudgement> getImpliedJudgements(IQueryMetaContext context) {
        return PositivePatternCall.getTypesImpliedByCall(supplierKey, variablesTuple);
    }

}