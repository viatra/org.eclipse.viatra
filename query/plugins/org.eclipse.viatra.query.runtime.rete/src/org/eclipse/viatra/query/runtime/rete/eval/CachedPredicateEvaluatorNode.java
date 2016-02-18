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
 *
 */
public class CachedPredicateEvaluatorNode extends OutputCachingEvaluatorNode {

	public CachedPredicateEvaluatorNode(ReteContainer reteContainer,
			Logger logger, IExpressionEvaluator evaluator,
            Map<String, Integer> parameterPositions,
			int sourceTupleWidth) {
		super(reteContainer, logger, evaluator, parameterPositions, sourceTupleWidth);
	}

	@Override
	protected Tuple tupleFromResult(Tuple incoming, Object evaluationresult) {
		return Boolean.TRUE.equals(evaluationresult) ? incoming : null;
	}

	@Override
	protected String logNodeName() {
		return "check()";
	}

}
