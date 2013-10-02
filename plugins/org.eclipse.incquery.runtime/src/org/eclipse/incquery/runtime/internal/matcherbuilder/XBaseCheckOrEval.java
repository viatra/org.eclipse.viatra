/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.internal.matcherbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred.BaseTypeSafeCheckOrEvalConstraint;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;
import org.eclipse.xtext.xbase.XExpression;

import com.google.inject.Injector;

/**
 * XExpression check/eval constraint: 
 * the given XExpression formed over the variables must evaluate to outputVariable (if given) or true (if outputVariable == null).
 */
@SuppressWarnings("restriction")
public class XBaseCheckOrEval<StubHandle> extends BaseTypeSafeCheckOrEvalConstraint<Pattern, StubHandle> {

    private final XExpression xExpression;
    private final EPMBodyToPSystem<StubHandle, ?> pGraph;
    private final Pattern pattern;

    /**
     * @param pSystem
     * @param affectedVariables
     */
    public XBaseCheckOrEval(EPMBodyToPSystem<StubHandle, ?> pGraph, XExpression xExpression, Pattern pattern, PVariable outputVariable) {
        super(pGraph.pSystem, getExternalPNodeReferencesOfXExpression(pGraph, xExpression), outputVariable);
        this.pGraph = pGraph;
        this.xExpression = xExpression;
        this.pattern = pattern;
    }

    @Override
    protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub) throws RetePatternBuildException {
        Set<Integer> affectedIndices = new HashSet<Integer>();
        Map<String, Integer> tupleNameMap = new HashMap<String, Integer>();
        Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
        List<Variable> variables = CorePatternLanguageHelper.getUsedVariables(xExpression, pGraph.body.getVariables());
        for (Variable variable : variables) {
            PVariable pNode = pGraph.getPNode(variable);
            Integer position = stub.getVariablesIndex().get(pNode);
            tupleNameMap.put(variable.getSimpleName(), position);
            affectedIndices.add(position);
        }
        int[] indices = new int[affectedIndices.size()];
        int k = 0;
        for (Integer index : affectedIndices)
            indices[k++] = index;

        XBaseEvaluator evaluator = new XBaseEvaluator(xExpression, tupleNameMap, pattern);
        injector.injectMembers(evaluator);
        evaluator.init();
        
        // TODO project to affected indices, then join back?
        
        if (outputVariable == null)
        	return buildable.buildPredicateChecker(evaluator, null, indices, stub);
        else 
        	return buildable.buildFunctionEvaluator(evaluator, stub, outputVariable);        	
    }

    private static Set<PVariable> getExternalPNodeReferencesOfXExpression(EPMBodyToPSystem<?, ?> pGraph,
            XExpression xExpression) {
        Set<PVariable> result = new HashSet<PVariable>();
        List<Variable> variables = CorePatternLanguageHelper.getUsedVariables(xExpression, pGraph.body.getVariables());
        for (Variable variable : variables) {
            result.add(pGraph.getPNode(variable));
        }
        return result;
    }

    @Override
    protected String toStringRest() {
        return new FlatTuple(new ArrayList<PVariable>(inputVariables).toArray()).toString() + "|="
                + xExpression.toString();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.rete.construction.psystem.BasePConstraint#getFunctionalDependencies()
     */
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	if (outputVariable == null) 
    		return Collections.emptyMap();
    	else 
    		return Collections.singletonMap(inputVariables, Collections.singleton(outputVariable));
    }


}
