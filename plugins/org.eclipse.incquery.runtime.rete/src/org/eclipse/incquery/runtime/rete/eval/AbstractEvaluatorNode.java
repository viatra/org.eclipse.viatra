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
package org.eclipse.incquery.runtime.rete.eval;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.single.SingleInputNode;
import org.eclipse.incquery.runtime.rete.tuple.TupleValueProvider;

/**
 * @author Bergmann Gabor
 *
 */
public abstract class AbstractEvaluatorNode extends SingleInputNode {
	
	protected abstract Tuple tupleFromResult(Tuple incoming, Object evaluationresult); 
//	protected abstract Iterable<Tuple> allTuples(); 
	/**
	 * E.g. "eval()"
	 */
	protected abstract String logNodeName(); 
	

	
    protected Logger logger;
    protected IExpressionEvaluator evaluator;    
    int sourceTupleWidth;
    private Map<String, Integer> parameterPositions;
    
    
    public AbstractEvaluatorNode(ReteContainer reteContainer, Logger logger, IExpressionEvaluator evaluator,
            Map<String, Integer> parameterPositions, int sourceTupleWidth) {
		super(reteContainer);
		this.logger = logger;
		this.evaluator = evaluator;
        this.parameterPositions = parameterPositions;
		this.sourceTupleWidth = sourceTupleWidth;
	}
//    protected Map<Tuple, Object> cachedResults = CollectionsFactory.getMap(); 
	
//	/* (non-Javadoc)
//	 * @see org.eclipse.incquery.runtime.rete.network.Supplier#pullInto(java.util.Collection)
//	 */
//	@Override
//	public void pullInto(Collection<Tuple> collector) {
//		for (Tuple tuple : allTuples()) {
//			collector.add(tuple);
//		}
//	}
	
    protected Object evaluateTerm(Tuple ps) {
//        // clearing ASMfunction traces
//        clearTraces(ps);

        // actual evaluation
        Object result = null;
        try {
            TupleValueProvider tupleParameters = new TupleValueProvider(ps, parameterPositions);
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

//        // saving ASMFunction traces
//        saveTraces(ps, evaluator.getTraces());

        return result;
    }
    
	protected String prettyPrintTuple(Tuple ps) {
        return ps.toString();
    }
	protected Object errorResult() {return null; }

	
}
