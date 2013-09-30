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

import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.single.SingleInputNode;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

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
	

	
    protected ReteEngine<?> engine;
    protected AbstractEvaluator evaluator;    
    int sourceTupleWidth;
    
    
	public AbstractEvaluatorNode(ReteContainer reteContainer,
			ReteEngine<?> engine, AbstractEvaluator evaluator, int sourceTupleWidth) {
		super(reteContainer);
		this.engine = engine;
		this.evaluator = evaluator;
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
            result = evaluator.evaluate(ps);
        } catch (Throwable e) { // NOPMD
            if (e instanceof Error)
                throw (Error) e;
            engine.getContext()
                    .logWarning(
                            String.format(
                                    "The incremental pattern matcher encountered an error during %s evaluation for pattern(s) %s over values %s. Error message: %s. (Developer note: %s in %s)",
                                    logNodeName(), getTraceInfoPatternsEnumerated(), prettyPrintTuple(ps), e.getMessage(), e
                                            .getClass().getSimpleName(), this), e);
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
