/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import java.util.Set;

import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

import com.google.common.collect.Sets;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class SurrogateQueryRewriter extends PDisjunctionRewriter {

    @Override
    public PDisjunction rewrite(PDisjunction disjunction) throws RewriterException {
        Set<PBody> replacedBodies = Sets.newHashSet();
        for (PBody body : disjunction.getBodies()) {
            PBodyCopier copier = new PBodyCopier(body) {

                @Override
                protected void copyTypeBinaryConstraint(TypeBinary typeBinary) {
                    Object[] elements = typeBinary.getVariablesTuple().getElements();
                    PVariable source = (PVariable) elements[0];
                    PVariable target = (PVariable) elements[1];
                    final Object typeKey = typeBinary.getSupplierKey();
                    if(SurrogateQueryRegistry.instance().hasSurrogateQueryFQN(typeKey)) {
                        PQuery surrogateQuery = SurrogateQueryRegistry.instance().getSurrogateQuery(typeKey);
                        if (surrogateQuery == null) {
                            throw new IllegalStateException(String.format("Surrogate query for feature %s not found", typeBinary.getTypeString()));
                        }
                        Tuple variablesTuple = new FlatTuple(variableMapping.get(source), variableMapping.get(target));
                        new PositivePatternCall(getCopiedBody(), variablesTuple, surrogateQuery);
                    } else {
                        new TypeBinary(getCopiedBody(), typeBinary.getContext(), variableMapping.get(source),
                            variableMapping.get(target), typeKey, typeBinary.getTypeString());
                    }
                }

            };
            PBody modifiedBody = copier.getCopiedBody();
            replacedBodies.add(modifiedBody);
            modifiedBody.setStatus(PQueryStatus.OK);
        }
        return new PDisjunction(replacedBodies);
    }

}
