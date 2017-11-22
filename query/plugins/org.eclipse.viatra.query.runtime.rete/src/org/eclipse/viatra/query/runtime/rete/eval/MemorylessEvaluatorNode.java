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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * @author Bergmann Gabor
 *
 */
public class MemorylessEvaluatorNode extends AbstractEvaluatorNode {

    /**
     * @since 1.5
     */
    public MemorylessEvaluatorNode(ReteContainer reteContainer, EvaluatorCore core) {
        super(reteContainer, core);
    }

    @Override
    public void pullInto(Collection<Tuple> collector) {
        Collection<Tuple> parentTuples = new ArrayList<Tuple>();
        propagatePullInto(parentTuples);
        for (Tuple incomingTuple : parentTuples) {
            Tuple evaluated = core.performEvaluation(incomingTuple);
            if (evaluated != null) 
                collector.add(evaluated);
        }
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
        Tuple evaluated = core.performEvaluation(updateElement);
        if (evaluated != null) 
            propagateUpdate(direction, evaluated);
    }

}
