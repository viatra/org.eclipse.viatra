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

import java.util.Collection;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Clearable;

/**
 * @author Bergmann Gabor
 *
 */
public abstract class OutputCachingEvaluatorNode extends AbstractEvaluatorNode implements Clearable {
	
	public OutputCachingEvaluatorNode(ReteContainer reteContainer,
			ReteEngine engine, IExpressionEvaluator evaluator,
            Map<String, Integer> parameterPositions, int sourceTupleWidth) {
		super(reteContainer, engine, evaluator, parameterPositions, sourceTupleWidth);
		reteContainer.registerClearable(this);
	}

	Map<Tuple, Tuple> outputCache = CollectionsFactory.getMap();
	//Map<Tuple, SoftReference<Object>> opportunisticCacheResults = new WeakHashMap<Tuple, SoftReference<Object>>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.incquery.runtime.rete.tuple.Clearable#clear()
	 */
	@Override
	public void clear() {
		outputCache.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.runtime.rete.network.Supplier#pullInto(java.util.Collection)
	 */
	@Override
	public void pullInto(Collection<Tuple> collector) {
		for (Tuple output : outputCache.values()) {
			collector.add(output);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.runtime.rete.network.Receiver#update(org.eclipse.incquery.runtime.rete.network.Direction, org.eclipse.incquery.runtime.rete.tuple.Tuple)
	 */
	@Override
	public void update(Direction direction, Tuple updateElement) {
		switch (direction) {
			case INSERT:
				final Tuple insertedOutput = tupleFromResult(updateElement, evaluateTerm(updateElement));
				if (insertedOutput != null) {
					outputCache.put(updateElement, insertedOutput);
					propagateUpdate(direction, insertedOutput);
				}
				break;
			case REVOKE:
				final Tuple revokedOutput = outputCache.remove(updateElement);
				if (revokedOutput != null) {
					propagateUpdate(direction, revokedOutput);
				}				
		}
	}

	// TODO
//    @Override
//    public ProjectionIndexer constructIndex(TupleMask mask) {
//        if (Options.employTrivialIndexers) {
//            if (mask.isPermutationOf(sourceTupleWidth))
//                return getPermutationIndexer(mask);
//            if (nullMask.equals(mask))
//                return getNullIndexer();
//        }
//        return super.constructIndex(mask);
//    }
    
}
