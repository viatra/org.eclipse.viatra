/*******************************************************************************
 * Copyright (c) 2010-2015, Csaba Debreceni, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.transformation.views.traceability.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.transformation.views.traceability.patterns.Trace;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

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
    private Multimap<PParameter, PParameter> traceSources;
    
    private String traceabilityId;

    
    /**
     * @throws ViatraQueryRuntimeException
     */
    public GenericTracedPQuery(GenericReferencedPQuery referencedQuery, Multimap<PParameter, PParameter> traceSources) {
        super(referencedQuery);
        this.referencedQuery = referencedQuery;
        this.traceSources = traceSources;
        this.traceabilityId = referencedQuery.getTraceabilityId();
        ensureInitialized();        
    }

    @Override
    protected Set<PBody> doGetContainedBodies() {
        PBody body = super.doGetContainedBodies().iterator().next();
        final PVariable var_trace = body.getOrCreateVariableByName(TRACE_PARAMETER);
        PVariable var_id = body.getOrCreateVariableByName(referencedQuery.getFullyQualifiedName());
        PVariable var_su = body.getOrCreateVariableByName("_");
        PVariable var_traceability = body.newConstantVariable(traceabilityId);
        
        List<ExportedParameter> symbolicParameters = body.getSymbolicParameters();
        Collection<String> baseParameters = traceSources.values().stream().map(PParameter::getName).collect(Collectors.toList());
        symbolicParameters.add(new ExportedParameter(body, var_trace, traceParameter));
        getParameters().add(new PParameter(var_trace.getName()));
        body.setSymbolicParameters(symbolicParameters);
        
        for (ExportedParameter parameter : symbolicParameters) {
            if(baseParameters.contains(parameter.getParameterName())) {
                new PositivePatternCall(body, Tuples.wideFlatTupleOf(parameter.getParameterVariable(), var_id, var_su, var_trace, var_traceability),
                        Trace.instance().getInternalQueryRepresentation());
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
