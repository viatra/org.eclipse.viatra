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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.views.traceability.patterns.Trace;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * This PQuery class defines an extended query for the given base query with the additionally referenced parameters.
 * <pre>pattern baseQuery&lt;referenced&gt;(&lt;base parameters&gt;, &lt;referenced parameters&gt;)</pre>
 * 
 * This PQuery selects the referenced parameters to apply the appear operation properly
 * 
 * @author Csaba Debreceni
 *
 */
public class GenericReferencedPQuery extends BaseGeneratedEMFPQuery {

    private static final String DEFAULT_SUBPACKAGE = "ref";
    private static final String DEFAULT_POSTFIX = "<referenced>";
    private List<PParameter> parameters;
    private Multimap<PParameter, PParameter> traceSources;
    private Map<PParameter, String> traceIds;
    protected int singleUseCounter = 0;

    private String traceabilityId;
    
    private PQuery baseQuery;

    public GenericReferencedPQuery(GenericReferencedPQuery referencedQuery) throws QueryInitializationException {
        baseQuery = referencedQuery.baseQuery;
        parameters = Lists.newArrayList(referencedQuery.parameters);
        traceSources = referencedQuery.traceSources;
        traceIds = referencedQuery.traceIds;
        traceabilityId = referencedQuery.traceabilityId;
    }
    
    public GenericReferencedPQuery(PQuery baseQuery, Multimap<PParameter, PParameter> traceSources,
            Map<PParameter, String> traceIds, String traceabilityId) throws QueryInitializationException {
        this.baseQuery = baseQuery;
        this.parameters = Lists.newArrayList(baseQuery.getParameters());
        this.parameters.addAll(traceSources.keySet());
        this.traceSources = traceSources;
        this.traceIds = traceIds;
        this.traceabilityId = traceabilityId;
        ensureInitialized();
    }
    
    @Override
    public String getFullyQualifiedName() {
        String fqn = baseQuery.getFullyQualifiedName();
        int i = fqn.lastIndexOf('.');
        if (i == -1) {
            return String.format("%s.%s%s<%s>", DEFAULT_SUBPACKAGE, fqn, DEFAULT_POSTFIX, traceabilityId);
        }
        String prefix = fqn.substring(0, i);
        String name = fqn.substring(i);
        return String.format("%s.%s%s%s<%s>", prefix, DEFAULT_SUBPACKAGE, name, DEFAULT_POSTFIX, traceabilityId);
    }

    @Override
    public List<PParameter> getParameters() {
        return parameters;
    }

    @Override
    protected Set<PBody> doGetContainedBodies() throws QueryInitializationException {
        PBody body = new PBody(this);

        List<PParameter> originalParams = baseQuery.getParameters();
        List<PVariable> newVariables = Lists.newArrayList();
        List<ExportedParameter> symbolicParameters = Lists.newArrayList();

        for (PParameter originalParam : originalParams) {
            PVariable var_param = body.getOrCreateVariableByName(originalParam.getName());
            symbolicParameters.add(new ExportedParameter(body, var_param, originalParam));
            newVariables.add(var_param);
        }

        body.setSymbolicParameters(symbolicParameters);
        new PositivePatternCall(body, Tuples.flatTupleOf(newVariables.toArray()), baseQuery);
        try {
            insertTraceCall(body);
        } catch (ViatraQueryException e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(GenericReferencedPQuery.class);
            logger.error(e.getMessage());
        }

        return Collections.<PBody> singleton(body);
    }

    private void insertTraceCall(PBody body) throws ViatraQueryException {
        for (PParameter pParameter : traceSources.keySet()) {
            insertTraceCall(body, pParameter);
        }
    }

    private void insertTraceCall(PBody body, PParameter pTarget) throws ViatraQueryException {
        PVariable var_target = body.getOrCreateVariableByName(pTarget.getName());
        body.getSymbolicParameters().add(new ExportedParameter(body, var_target, pTarget));

        PVariable var_id = null;
        if (traceIds.containsKey(pTarget))
            var_id = body.newConstantVariable(traceIds.get(pTarget));
        else
            var_id = body.getOrCreateVariableByName("_<" + (++singleUseCounter) + ">");

        for (PParameter pSource : traceSources.get(pTarget)) {
            PVariable var_source = body.getOrCreateVariableByName(pSource.getName());
            PVariable var_ = body.getOrCreateVariableByName("_<" + (++singleUseCounter) + ">");
            PVariable var_trace1 = body.getOrCreateVariableByName("_<" + (++singleUseCounter) + ">");
            PVariable var_trace2 = body.getOrCreateVariableByName("_<" + (++singleUseCounter) + ">");
            PVariable var_traceability = body.newConstantVariable(traceabilityId);
            
            
            new PositivePatternCall(body, Tuples.wideFlatTupleOf(var_source, var_id, var_target, var_trace1, var_traceability),
                    Trace.instance().getInternalQueryRepresentation());
            new NegativePatternCall(body, Tuples.wideFlatTupleOf(var_target, var_id, var_, var_trace2, var_traceability),
                    Trace.instance().getInternalQueryRepresentation());
        }
        if (pTarget.getTypeName() != null) {
            //TODO: resolve hack
            String[] type = pTarget.getTypeName().split(Pattern.quote("||"));
            new TypeConstraint(body, Tuples.staticArityFlatTupleOf(var_target), 
                    toInputKey(getClassifierLiteral(type[0], type[1]))/*, 
                    String.format("{0}/{1}", type[0], type[1])*/);
        }
    }
    
    private static IInputKey toInputKey(EClassifier classifierLiteral) {
        if (classifierLiteral instanceof EClass)
            return new EClassTransitiveInstancesKey((EClass) classifierLiteral);
        else if (classifierLiteral instanceof EDataType)
            return new EDataTypeInSlotsKey((EDataType) classifierLiteral);
        else return null;
    }

    @Override
    protected EClassifier getClassifierLiteral(String packageUri, String classifierName) {
        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageUri);
        Preconditions.checkState(ePackage != null, "EPackage %s not found in EPackage Registry.", packageUri);
        EClassifier literal = ePackage.getEClassifier(classifierName);
        Preconditions.checkState(literal != null, "Classifier %s not found in EPackage %s", classifierName,
                packageUri);
        return literal;
    }

    protected Collection<String> getBaseParameters() {
        return baseQuery.getParameterNames();
    }
    
    public Multimap<PParameter, PParameter> getReferenceSources() {
        return traceSources;
    }

    public final Set<PParameter> getReferenceParameters() {
        return traceSources.keySet();
    }

    public String getTraceabilityId() {
        return traceabilityId;
    }
}
