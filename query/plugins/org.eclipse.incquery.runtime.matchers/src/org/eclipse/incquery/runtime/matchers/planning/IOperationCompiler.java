/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.planning;

import java.util.Map;

import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;

/**
 * 
 * An implicit common parameter is the "effort" PatternDescription. This
 * indicates that the build request is part of an effort to build the matcher of
 * the given pattern; it it important to record this during code generation so
 * that the generated code can be separated according to patterns.
 * 
 * @param <Collector>
 *            the handle of a receiver-like RETE ending to which plans can be
 *            connected
 * @author Gabor Bergmann
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IOperationCompiler<Collector> {

    public Collector patternCollector(PQuery pattern) throws QueryProcessingException;
 
    public void buildConnection(SubPlan parentPlan, Collector collector);
    
    /**
	 * @since 0.9
	 */
    public void patternFinished(PQuery pattern, Collector collector);
    
    public SubPlan patternCallPlan(Tuple nodes, PQuery supplierKey)
            throws QueryProcessingException;

    public SubPlan transitiveInstantiationPlan(Tuple nodes);

    public SubPlan directInstantiationPlan(Tuple nodes);

    public SubPlan transitiveGeneralizationPlan(Tuple nodes);

    public SubPlan directGeneralizationPlan(Tuple nodes);

    public SubPlan transitiveContainmentPlan(Tuple nodes);

    public SubPlan directContainmentPlan(Tuple nodes);

    public SubPlan binaryEdgeTypePlan(Tuple nodes, Object supplierKey);

    public SubPlan ternaryEdgeTypePlan(Tuple nodes, Object supplierKey);

    public SubPlan unaryTypePlan(Tuple nodes, Object supplierKey);

    public SubPlan buildStartingPlan(Object[] constantValues, Object[] constantNames);

    public SubPlan buildEqualityChecker(SubPlan parentPlan, int[] indices);

    public SubPlan buildInjectivityChecker(SubPlan parentPlan, int subject, int[] inequalIndices);

    public SubPlan buildTransitiveClosure(SubPlan parentPlan);

    public SubPlan buildTrimmer(SubPlan parentPlan, TupleMask trimMask, boolean enforceUniqueness);

    public SubPlan buildBetaNode(SubPlan primaryPlan, SubPlan sidePlan,
            TupleMask primaryMask, TupleMask sideMask, TupleMask complementer, boolean negative);

    public SubPlan buildCounterBetaNode(SubPlan primaryPlan, SubPlan sidePlan,
            TupleMask primaryMask, TupleMask originalSideMask, TupleMask complementer,
            Object aggregateResultCalibrationElement);

    public SubPlan buildCountCheckBetaNode(SubPlan primaryPlan, SubPlan sidePlan,
            TupleMask primaryMask, TupleMask originalSideMask, int resultPositionInSignature);

    public SubPlan buildPredicateChecker(IExpressionEvaluator evaluator, Map<String, Integer> tupleNameMap,
            SubPlan parentPlan);
    public SubPlan buildFunctionEvaluator(IExpressionEvaluator evaluator, Map<String, Integer> tupleNameMap,
            SubPlan parentPlan, Object computedResultCalibrationElement);
    
    /**
     * @return an operation compiler that potentially acts on a separate container
     */
    public IOperationCompiler<Collector> getNextContainer();

    /**
     * @return an operation compiler that puts build actions on the tab of the given pattern
     * @since 0.9
     */
    public IOperationCompiler<Collector> putOnTab(PQuery effort /*, IPatternMatcherContext context*/);

    public void reinitialize();

}