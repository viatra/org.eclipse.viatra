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

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

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
            	protected void copyTypeConstraint(TypeConstraint typeConstraint) {
                    PVariable[] mappedVariables = extractMappedVariables(typeConstraint);
                    FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables); 	
                    final IInputKey supplierKey = typeConstraint.getSupplierKey();
                    if(SurrogateQueryRegistry.instance().hasSurrogateQueryFQN(supplierKey)) {
                        PQuery surrogateQuery = SurrogateQueryRegistry.instance().getSurrogateQuery(supplierKey);
                        if (surrogateQuery == null) {
                            throw new IllegalStateException(
                            		String.format("Surrogate query for feature %s not found", 
                            				supplierKey.getPrettyPrintableName()));
                        }
                        new PositivePatternCall(getCopiedBody(), variablesTuple, surrogateQuery);
                    } else {
                    	new TypeConstraint(getCopiedBody(), variablesTuple, supplierKey);
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
