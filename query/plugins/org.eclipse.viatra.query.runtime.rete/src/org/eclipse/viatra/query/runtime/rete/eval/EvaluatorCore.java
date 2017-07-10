/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.eval;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.tuple.LeftInheritanceTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.rete.tuple.TupleValueProvider;

/**
 * @author Bergmann Gabor
 * @since 1.5
 */
public abstract class EvaluatorCore {
    
    public abstract Tuple tupleFromResult(Tuple incoming, Object evaluationresult); 
    /**
     * E.g. "eval()"
     */
    protected abstract String evaluationKind(); 
    

    protected Logger logger;
    protected IExpressionEvaluator evaluator;    
    int sourceTupleWidth;
    private Map<String, Integer> parameterPositions;
    protected IQueryRuntimeContext runtimeContext;
    
    protected IEvaluatorNode evaluatorNode;
    
    public EvaluatorCore(Logger logger, IExpressionEvaluator evaluator,
            Map<String, Integer> parameterPositions, int sourceTupleWidth) {
        this.logger = logger;
        this.evaluator = evaluator;
        this.parameterPositions = parameterPositions;
        this.sourceTupleWidth = sourceTupleWidth;
    }
    
    public void init(IEvaluatorNode evaluatorNode) {
        this.evaluatorNode = evaluatorNode;
        runtimeContext = evaluatorNode.getReteContainer().getNetwork().getEngine().getRuntimeContext();
    }
    
    public Tuple performEvaluation(Tuple inputTuple) {
        return tupleFromResult(inputTuple, evaluateTerm(inputTuple));
    }
    
    public Object evaluateTerm(Tuple ps) {
        // actual evaluation
        Object result = null;
        try {
            TupleValueProvider tupleParameters = new TupleValueProvider(runtimeContext.unwrapTuple(ps), parameterPositions);
            result = evaluator.evaluateExpression(tupleParameters);
        } catch (Exception e) {
            logger.warn(
                    String.format(
                            "The incremental pattern matcher encountered an error during %s evaluation for pattern(s) %s over values %s. Error message: %s. (Developer note: %s in %s)",
                            evaluationKind(), 
                            evaluatorNode.prettyPrintTraceInfoPatternList(), 
                            prettyPrintTuple(ps), 
                            e.getMessage(), e.getClass().getSimpleName(), 
                            this.evaluatorNode
                    ), 
            e);

            result = errorResult();
        }

        return result;
    }
    
    protected String prettyPrintTuple(Tuple ps) {
        return ps.toString();
    }
    protected Object errorResult() {return null; }

    public static class PredicateEvaluatorCore extends EvaluatorCore {

        public PredicateEvaluatorCore(Logger logger, IExpressionEvaluator evaluator,
                Map<String, Integer> parameterPositions, int sourceTupleWidth) {
            super(logger, evaluator, parameterPositions, sourceTupleWidth);
        }

        @Override
        public Tuple tupleFromResult(Tuple incoming, Object evaluationresult) {
            return Boolean.TRUE.equals(evaluationresult) ? incoming : null;
        }

        @Override
        protected String evaluationKind() {
            return "check()";
        }
        
    }
    
    public static class FunctionEvaluatorCore extends EvaluatorCore {

        public FunctionEvaluatorCore(Logger logger, IExpressionEvaluator evaluator,
                Map<String, Integer> parameterPositions, int sourceTupleWidth) {
            super(logger, evaluator, parameterPositions, sourceTupleWidth);
        }

        @Override
        public Tuple tupleFromResult(Tuple incoming, Object evaluationresult) {
            if (evaluationresult == null) return null;
            return Tuples.staticArityLeftInheritanceTupleOf(incoming, 
                    runtimeContext.wrapElement(evaluationresult));
        }

        @Override
        protected String evaluationKind() {
            return "eval()";
        }
        
    }
}
