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
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * @author Bergmann Gabor
 * @deprecated Use {@link OutputCachingEvaluatorNode} (non-abstract since 1.5) with {@link EvaluationCore}.
 */
@Deprecated
public class CachedPredicateEvaluatorNode extends OutputCachingEvaluatorNode {

	public CachedPredicateEvaluatorNode(ReteContainer reteContainer,
			Logger logger, IExpressionEvaluator evaluator,
            Map<String, Integer> parameterPositions,
			int sourceTupleWidth) {
		super(reteContainer, new EvaluatorCore.PredicateEvaluatorCore(logger, evaluator, parameterPositions, sourceTupleWidth));
	}

    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
	@Override
	protected Tuple tupleFromResult(Tuple incoming, Object evaluationresult) {
		return Boolean.TRUE.equals(evaluationresult) ? incoming : null;
	}

    /**
     * @deprecated use {@link EvaluationCore}
     */
    @Deprecated
	@Override
	protected String logNodeName() {
		return "check()";
	}

}
