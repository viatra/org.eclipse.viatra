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
import org.eclipse.incquery.runtime.rete.tuple.LeftInheritanceTuple;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * @author Bergmann Gabor
 *
 */
public class CachedFunctionEvaluatorNode extends OutputCachingEvaluatorNode {

	public CachedFunctionEvaluatorNode(ReteContainer reteContainer,
			ReteEngine<?> engine, AbstractEvaluator evaluator,
			int sourceTupleWidth) {
		super(reteContainer, engine, evaluator, sourceTupleWidth);
	}

	@Override
	protected Tuple tupleFromResult(Tuple incoming, Object evaluationresult) {
		if (evaluationresult == null) return null;
		return new LeftInheritanceTuple(incoming, new Object[]{evaluationresult});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.runtime.rete.eval.AbstractEvaluatorNode#logNodeName()
	 */
	@Override
	protected String logNodeName() {
		return "eval()";
	}

}
