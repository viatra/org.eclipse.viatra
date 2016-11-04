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
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.single.SingleInputNode;
import org.eclipse.viatra.query.runtime.rete.tuple.TupleValueProvider;

/**
 * @author Bergmann Gabor
 */
public abstract class AbstractEvaluatorNode extends SingleInputNode implements IEvaluatorNode {
    
    /**
     * @since 1.5
     */
    protected EvaluatorCore core;


    /**
     * @since 1.5
     */
    public AbstractEvaluatorNode(ReteContainer reteContainer, EvaluatorCore core) {
        super(reteContainer);
        this.core = core;
        core.init(this);
    }
    
    /**
     * @since 1.5
     */
    @Override
    public ReteContainer getReteContainer() {
        return getContainer();
    }
    
    /**
     * @since 1.5
     */
    @Override
    public String prettyPrintTraceInfoPatternList() {
        return getTraceInfoPatternsEnumerated();
    }
    
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected abstract Tuple tupleFromResult(Tuple incoming, Object evaluationresult); 
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected abstract String logNodeName(); 
    

    
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected Logger logger;
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected IExpressionEvaluator evaluator;    
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    int sourceTupleWidth;
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    private Map<String, Integer> parameterPositions;
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected IQueryRuntimeContext runtimeContext;
    
    
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    public AbstractEvaluatorNode(ReteContainer reteContainer, Logger logger, IExpressionEvaluator evaluator,
            Map<String, Integer> parameterPositions, int sourceTupleWidth) {
        super(reteContainer);
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected Object evaluateTerm(Tuple ps) {
        // actual evaluation
        Object result = null;
        try {
            TupleValueProvider tupleParameters = new TupleValueProvider(runtimeContext.unwrapTuple(ps), parameterPositions);
            result = evaluator.evaluateExpression(tupleParameters);
        } catch (Exception e) {
            logger.warn(
                    String.format(
                            "The incremental pattern matcher encountered an error during %s evaluation for pattern(s) %s over values %s. Error message: %s. (Developer note: %s in %s)",
                            logNodeName(), 
                            getTraceInfoPatternsEnumerated(), 
                            prettyPrintTuple(ps), 
                            e.getMessage(), e.getClass().getSimpleName(), 
                            this
                    ), 
            e);
            // engine.logEvaluatorException(e);

            result = errorResult();
        }

        return result;
    }
    
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected String prettyPrintTuple(Tuple ps) {
        return ps.toString();
    }
    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
    protected Object errorResult() {return null; }
	

	
}
