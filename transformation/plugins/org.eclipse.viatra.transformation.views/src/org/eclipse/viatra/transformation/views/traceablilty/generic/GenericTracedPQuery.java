/*******************************************************************************
 * Copyright (c) 2010-2015, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.transformation.views.traceablilty.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.views.traceability.patterns.util.TraceQuerySpecification;

import com.google.common.collect.Lists;

/**
 * This PQuery class defines an extended query for the given referenced query with the additional trace object.
 * <pre>pattern baseQuery&lt;referenced&gt;&lt;traced&gt;(&lt;base parameters&gt;, &lt;referenced parameters&gt;, &lt;trace&gt;)</pre>
 
 * Required to select matches based on the notification of the Trace object.
 * @author Csaba Debreceni
 *
 */
public class GenericTracedPQuery extends GenericReferencedPQuery {

    private GenericReferencedPQuery referencedQuery;

    private static final String DEFAULT_SUBPACKAGE = "traced";
    private static final String DEFAULT_POSTFIX = "<traced>";
    public static final String TRACE_PARAMETER = "<trace>";
    final PParameter traceParameter = new PParameter(TRACE_PARAMETER);
    
    private String traceabilityId;
    
    public GenericTracedPQuery(GenericReferencedPQuery referencedQuery) throws QueryInitializationException {
        super(referencedQuery);
        this.referencedQuery = referencedQuery;
        this.traceabilityId = referencedQuery.getTraceabilityId();
        ensureInitialized();        
    }

    @Override
    protected Set<PBody> doGetContainedBodies() throws QueryInitializationException {
        PBody body = super.doGetContainedBodies().iterator().next();
        final PVariable var_trace = body.getOrCreateVariableByName(TRACE_PARAMETER);
        PVariable var_id = body.getOrCreateVariableByName(referencedQuery.getFullyQualifiedName());
        PVariable var_su = body.getOrCreateVariableByName("_");
        PVariable var_traceability = body.newConstantVariable(traceabilityId);
        
        List<ExportedParameter> symbolicParameters = body.getSymbolicParameters();
        Collection<String> baseParameters = getBaseParameters();
        symbolicParameters.add(new ExportedParameter(body, var_trace, traceParameter));
        getParameters().add(new PParameter(var_trace.getName()));
        body.setSymbolicParameters(symbolicParameters);
        
        for (ExportedParameter parameter : symbolicParameters) {
            if(baseParameters.contains(parameter.getParameterName())) {
                try {
                    new PositivePatternCall(body, new FlatTuple(parameter.getParameterVariable(), var_id, var_su, var_trace, var_traceability),
                            TraceQuerySpecification.instance().getInternalQueryRepresentation());
                } catch (ViatraQueryException e) {
                    Logger logger = ViatraQueryLoggingUtil.getLogger(GenericTracedPQuery.class);
                    logger.error(e.getMessage());
                }
            }
        }
        
        return Collections.<PBody> singleton(body);
    }
    
    @Override
    public List<PParameter> getParameters() {
        ArrayList<PParameter> parameters = Lists.newArrayList(super.getParameters());
        parameters.add(traceParameter);
        return parameters;
    }

    @Override
    public String getFullyQualifiedName() {
        String fqn = referencedQuery.getFullyQualifiedName();
        int i = fqn.lastIndexOf('.');
        if (i == -1) {
            return String.format("%s.%s%s<%s>", DEFAULT_SUBPACKAGE, fqn, DEFAULT_POSTFIX, traceabilityId);
        }
        String prefix = fqn.substring(0, i);
        String name = fqn.substring(i);
        return String.format("%s.%s%s%s<%s>", prefix, DEFAULT_SUBPACKAGE, name, DEFAULT_POSTFIX, traceabilityId);
    }
    
    public void setTraceabilityId(String id) {
        traceabilityId = id;
    }
}
