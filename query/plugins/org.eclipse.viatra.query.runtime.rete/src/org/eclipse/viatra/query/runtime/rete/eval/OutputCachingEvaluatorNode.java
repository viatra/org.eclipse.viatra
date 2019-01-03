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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.Pair;
import org.eclipse.viatra.query.runtime.matchers.util.TimestampAwareMemory;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;

/**
 * An evaluator node that caches the evaluation result. This node is also capable of caching the timestamps associated
 * with the result tuples if it is used in recursive differential dataflow evaluation.
 * 
 * @author Bergmann Gabor
 * @author Tamas Szabo
 */
public class OutputCachingEvaluatorNode extends AbstractEvaluatorNode implements Clearable {

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic logic;

    Map<Tuple, Tuple> outputCache;

    /**
     * Maps input tuples to the associated timestamp. It is wrong to map evaluation result to timestamps because the
     * different input tuples may yield the same evaluation result. This field is null as long as this node is in a
     * non-recursive group.
     * 
     * @since 2.2
     */
    TimestampAwareMemory<DifferentialTimestamp> timestampMemory;

    /**
     * @since 1.5
     */
    public OutputCachingEvaluatorNode(final ReteContainer reteContainer, final EvaluatorCore core) {
        super(reteContainer, core);
        reteContainer.registerClearable(this);
        this.outputCache = CollectionsFactory.createMap();
        this.logic = createLogic();
    }

    @Override
    public void networkStructureChanged() {
        super.networkStructureChanged();
        this.logic = createLogic();
    }

    @Override
    public void clear() {
        this.outputCache.clear();
        if (this.timestampMemory != null) {
            this.timestampMemory.clear();
        }
    }
    
    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createLogic() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()
                && this.reteContainer.getCommunicationTracker().isInRecursiveGroup(this)) {
            if (this.timestampMemory == null) {
                this.timestampMemory = new TimestampAwareMemory<DifferentialTimestamp>();
            }
            return createRecursiveTimelyLogic();
        } else {
            return createDefaultLogic();
        }
    }
    
    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createDefaultLogic() {
        return this.DEFAULT;
    }

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createRecursiveTimelyLogic() {
        return this.RECURSIVE_TIMELY;
    }

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        this.logic.pullInto(collector, flush);
    }

    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector, final boolean flush) {
        this.logic.pullIntoWithTimestamp(collector, flush);
    }

    @Override
    public void update(final Direction direction, final Tuple input, final DifferentialTimestamp timestamp) {
        this.logic.update(direction, input, timestamp);
    }

    /**
     * @since 2.2
     */
    protected static abstract class NetworkStructureChangeSensitiveLogic {

        public abstract void update(final Direction direction, final Tuple input,
                final DifferentialTimestamp timestamp);

        public abstract void pullInto(final Collection<Tuple> collector, final boolean flush);

        public abstract void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector,
                final boolean flush);

    }

    private final NetworkStructureChangeSensitiveLogic DEFAULT = new NetworkStructureChangeSensitiveLogic() {

        @Override
        public void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector, final boolean flush) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void pullInto(final Collection<Tuple> collector, final boolean flush) {
            for (final Tuple output : outputCache.values()) {
                collector.add(output);
            }
        }

        @Override
        public void update(final Direction direction, final Tuple input, final DifferentialTimestamp timestamp) {
            if (direction == Direction.INSERT) {
                final Tuple output = core.performEvaluation(input);
                if (output != null) {
                    final Tuple previous = outputCache.put(input, output);
                    if (previous != null) {
                        throw new IllegalStateException(
                                String.format("Duplicate insertion of tuple %s into node %s", input, this));
                    }
                    propagateUpdate(direction, output, timestamp);
                }
            } else {
                final Tuple output = outputCache.remove(input);
                if (output != null) {
                    // may be null if no result was yielded
                    propagateUpdate(direction, output, timestamp);
                }
            }
        }
    };

    private final NetworkStructureChangeSensitiveLogic RECURSIVE_TIMELY = new NetworkStructureChangeSensitiveLogic() {

        @Override
        public void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector, final boolean flush) {
            for (final Entry<Tuple, DifferentialTimestamp> entry : timestampMemory.asMap().entrySet()) {
                // we need to report the smallest timestamp per output
                // it can happen though that we get the same result from different inputs
                final Tuple input = entry.getKey();
                final Tuple output = outputCache.get(input);
                final DifferentialTimestamp inputTimestamp = entry.getValue();
                final DifferentialTimestamp storedTimestamp = collector.get(output);
                if (storedTimestamp == null || inputTimestamp.compareTo(storedTimestamp) < 0) {
                    collector.put(output, inputTimestamp);
                }
            }
        }

        @Override
        public void pullInto(final Collection<Tuple> collector, final boolean flush) {
            DEFAULT.pullInto(collector, flush);
        }

        @Override
        public void update(final Direction direction, final Tuple input, final DifferentialTimestamp timestamp) {
            if (direction == Direction.INSERT) {
                Tuple output = outputCache.get(input);
                if (output == null) {
                    output = core.performEvaluation(input);
                }
                if (output != null) {
                    timestampMemory.put(input, timestamp);
                    outputCache.put(input, output);
                    propagateUpdate(direction, output, timestamp);
                }
            } else {
                final Tuple result = outputCache.get(input);
                if (result != null) {
                    // may be null if no result was yielded
                    final Pair<DifferentialTimestamp, DifferentialTimestamp> pair = timestampMemory.remove(input,
                            timestamp);
                    if (pair.second == null) {
                        outputCache.remove(input);
                    }
                    propagateUpdate(direction, result, timestamp);
                }
            }
        }
    };

}
