/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.eval;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.memories.TimestampReplacement;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.TimelyMemory;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;

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
     * Maps input tuples to the associated timestamps. It is wrong to map evaluation result to timestamps because the
     * different input tuples may yield the same evaluation result. This field is null as long as this node is in a
     * non-recursive group.
     * 
     * @since 2.2
     */
    TimelyMemory<Timestamp> timestampMemory;

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
                this.timestampMemory = new TimelyMemory<Timestamp>();
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
        return this.TIMELESS;
    }

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createRecursiveTimelyLogic() {
        return this.TIMELY;
    }

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        this.logic.pullInto(collector, flush);
    }

    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
        this.logic.pullIntoWithTimestamp(collector, flush);
    }

    @Override
    public void update(final Direction direction, final Tuple input, final Timestamp timestamp) {
        this.logic.update(direction, input, timestamp);
    }

    /**
     * @since 2.2
     */
    protected static abstract class NetworkStructureChangeSensitiveLogic {

        public abstract void update(final Direction direction, final Tuple input, final Timestamp timestamp);

        public abstract void pullInto(final Collection<Tuple> collector, final boolean flush);

        public abstract void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush);

    }

    private final NetworkStructureChangeSensitiveLogic TIMELESS = new NetworkStructureChangeSensitiveLogic() {

        @Override
        public void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void pullInto(final Collection<Tuple> collector, final boolean flush) {
            for (final Tuple output : outputCache.values()) {
                collector.add(output);
            }
        }

        @Override
        public void update(final Direction direction, final Tuple input, final Timestamp timestamp) {
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

    private final NetworkStructureChangeSensitiveLogic TIMELY = new NetworkStructureChangeSensitiveLogic() {

        @Override
        public void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
            for (final Entry<Tuple, Timestamp> entry : timestampMemory.asMap().entrySet()) {
                final Tuple input = entry.getKey();
                final Tuple output = outputCache.get(input);
                if (output != NORESULT) {
                    final Timestamp timestamp = entry.getValue();
                    collector.put(output, timestamp);
                }
            }
        }

        @Override
        public void pullInto(final Collection<Tuple> collector, final boolean flush) {
            TIMELESS.pullInto(collector, flush);
        }

        @Override
        public void update(final Direction direction, final Tuple input, final Timestamp timestamp) {
            if (direction == Direction.INSERT) {
                Tuple output = outputCache.get(input);
                if (output == null) {
                    output = core.performEvaluation(input);
                    if (output == null) {
                        // the evaluation result is really null
                        output = NORESULT;
                    }
                    outputCache.put(input, output);
                }

                timestampMemory.put(input, timestamp);

                if (output != NORESULT) {
                    propagateUpdate(direction, output, timestamp);
                }
            } else {
                final Tuple output = outputCache.get(input);
                final TimestampReplacement<Timestamp> pair = timestampMemory.remove(input, timestamp);
                if (pair.newValue == null) {
                    outputCache.remove(input);
                }
                if (output != NORESULT) {
                    propagateUpdate(direction, output, timestamp);
                }
            }
        }
    };

    /**
     * This field is used to represent the "null" evaluation result. This is an optimization used in the timely case
     * where the same tuple may be inserted multiple times with different timestamps. This way, we can also cache if
     * something evaluated to null, thus avoiding the need to re-run a potentially expensive evaluation.
     */
    private static final Tuple NORESULT = Tuples.staticArityFlatTupleOf(NoResult.INSTANCE);

    private enum NoResult {
        INSTANCE
    }

}
