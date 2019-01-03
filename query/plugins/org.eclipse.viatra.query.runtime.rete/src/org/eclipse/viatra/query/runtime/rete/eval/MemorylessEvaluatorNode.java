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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;

/**
 * @author Bergmann Gabor
 *
 */
public class MemorylessEvaluatorNode extends AbstractEvaluatorNode {

    /**
     * @since 1.5
     */
    public MemorylessEvaluatorNode(final ReteContainer reteContainer, final EvaluatorCore core) {
        super(reteContainer, core);
    }

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        final Collection<Tuple> parentTuples = new ArrayList<Tuple>();
        propagatePullInto(parentTuples, flush);
        for (final Tuple incomingTuple : parentTuples) {
            final Tuple evaluated = core.performEvaluation(incomingTuple);
            if (evaluated != null) {
                collector.add(evaluated);
            }
        }
    }

    @Override
    public void pullIntoWithTimestamp(Map<Tuple, DifferentialTimestamp> collector, boolean flush) {
        final Map<Tuple, DifferentialTimestamp> parentTuples = new HashMap<Tuple, DifferentialTimestamp>();
        propagatePullIntoWithTimestamp(parentTuples, flush);
        for (final Entry<Tuple, DifferentialTimestamp> entry : parentTuples.entrySet()) {
            final Tuple evaluated = core.performEvaluation(entry.getKey());
            if (evaluated != null) {
                collector.put(evaluated, entry.getValue());
            }
        }
    }

    @Override
    public void update(final Direction direction, final Tuple updateElement, final DifferentialTimestamp timestamp) {
        final Tuple evaluated = core.performEvaluation(updateElement);
        if (evaluated != null) {
            propagateUpdate(direction, evaluated, timestamp);
        }
    }

}
