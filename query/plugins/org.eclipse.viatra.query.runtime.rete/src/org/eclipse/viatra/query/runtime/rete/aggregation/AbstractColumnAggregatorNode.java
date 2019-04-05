/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, itemis AG, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.index.StandardIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.single.SingleInputNode;

/**
 * @author Tamas Szabo
 * @since 2.2
 *
 */
public abstract class AbstractColumnAggregatorNode<Domain, Accumulator, AggregateResult> extends SingleInputNode
        implements Clearable, IAggregatorNode {

    /**
     * @since 1.6
     */
    protected final IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator;

    /**
     * @since 1.6
     */
    protected final TupleMask groupMask;

    /**
     * @since 1.6
     */
    protected final TupleMask columnMask;

    /**
     * @since 1.6
     */
    protected final int sourceWidth;

    /**
     * @since 1.6
     */
    protected final IQueryRuntimeContext runtimeContext;

    protected final AggregateResult NEUTRAL;

    protected AggregatorOuterIndexer aggregatorOuterIndexer;

    @SuppressWarnings("rawtypes")
    protected AbstractColumnAggregatorNode.AggregatorOuterIdentityIndexer[] aggregatorOuterIdentityIndexers;

    /**
     * Creates a new column aggregator node.
     * 
     * @param reteContainer
     *            the RETE container of the node
     * @param operator
     *            the aggregation operator
     * @param deleteRederiveEvaluation
     *            true if the node should run in DRED mode, false otherwise
     * @param groupMask
     *            the mask that masks a tuple to obtain the key that we are grouping-by
     * @param columnMask
     *            the mask that masks a tuple to obtain the tuple element(s) that we are aggregating over
     * @param posetComparator
     *            the poset comparator for the column, if known, otherwise it can be null
     * @since 1.6
     */
    public AbstractColumnAggregatorNode(final ReteContainer reteContainer,
            final IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator,
            final TupleMask groupMask, final TupleMask columnMask) {
        super(reteContainer);
        this.operator = operator;
        this.groupMask = groupMask;
        this.columnMask = columnMask;
        this.sourceWidth = groupMask.indices.length;
        this.runtimeContext = reteContainer.getNetwork().getEngine().getRuntimeContext();
        this.NEUTRAL = operator.getAggregate(operator.createNeutral());
        reteContainer.registerClearable(this);
    }

    /**
     * Creates a new column aggregator node.
     * 
     * @param reteContainer
     *            the RETE container of the node
     * @param operator
     *            the aggregation operator
     * @param groupMask
     *            the mask that masks a tuple to obtain the key that we are grouping-by
     * @param aggregatedColumn
     *            the index of the column that the aggregator node is aggregating over
     */
    public AbstractColumnAggregatorNode(final ReteContainer reteContainer,
            final IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator,
            final TupleMask groupMask, final int aggregatedColumn) {
        this(reteContainer, operator, groupMask, TupleMask.selectSingle(aggregatedColumn, groupMask.sourceWidth));
    }

    @Override
    public CommunicationTracker getCommunicationTracker() {
        return this.reteContainer.getCommunicationTracker();
    }

    @Override
    public void pullInto(Collection<Tuple> collector, boolean flush) {
        // DIRECT CHILDREN NOT SUPPORTED
        throw new UnsupportedOperationException();
    }

    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
        // DIRECT CHILDREN NOT SUPPORTED
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendChild(Receiver receiver) {
        // DIRECT CHILDREN NOT SUPPORTED
        throw new UnsupportedOperationException();
    }

    @Override
    public Indexer getAggregatorOuterIndexer() {
        if (aggregatorOuterIndexer == null) {
            aggregatorOuterIndexer = new AggregatorOuterIndexer();
            this.getCommunicationTracker().registerDependency(this, aggregatorOuterIndexer);
        }
        return aggregatorOuterIndexer;
    }

    @Override
    public Indexer getAggregatorOuterIdentityIndexer(final int resultPositionInSignature) {
        if (aggregatorOuterIdentityIndexers == null) {
            aggregatorOuterIdentityIndexers = new AbstractColumnAggregatorNode.AggregatorOuterIdentityIndexer[sourceWidth + 1];
        }
        if (aggregatorOuterIdentityIndexers[resultPositionInSignature] == null) {
            aggregatorOuterIdentityIndexers[resultPositionInSignature] = new AggregatorOuterIdentityIndexer(
                    resultPositionInSignature);
            this.getCommunicationTracker().registerDependency(this,
                    aggregatorOuterIdentityIndexers[resultPositionInSignature]);
        }
        return aggregatorOuterIdentityIndexers[resultPositionInSignature];
    }

    /**
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public void propagate(final Tuple group, final AggregateResult oldValue, final AggregateResult newValue, final Timestamp timestamp) {
        if (!Objects.equals(oldValue, newValue)) {
            final Tuple oldResultTuple = tupleFromAggregateResult(group, oldValue);
            final Tuple newResultTuple = tupleFromAggregateResult(group, newValue);

            if (aggregatorOuterIndexer != null) {
                aggregatorOuterIndexer.propagate(group, oldResultTuple, newResultTuple, timestamp);
            }
            if (aggregatorOuterIdentityIndexers != null) {
                for (final AggregatorOuterIdentityIndexer aggregatorOuterIdentityIndexer : aggregatorOuterIdentityIndexers) {
                    if (aggregatorOuterIdentityIndexer != null) {
                        aggregatorOuterIdentityIndexer.propagate(group, oldResultTuple, newResultTuple, timestamp);
                    }
                }
            }
        }
    }

    public abstract Tuple getAggregateTuple(final Tuple key);

    public abstract AggregateResult getAggregateResult(final Tuple key);

    protected Tuple tupleFromAggregateResult(final Tuple groupTuple, final AggregateResult aggregateResult) {
        if (aggregateResult == null) {
            return null;
        } else {
            return Tuples.staticArityLeftInheritanceTupleOf(groupTuple, runtimeContext.wrapElement(aggregateResult));
        }
    }

    /**
     * A special non-iterable index that retrieves the aggregated, packed result (signature+aggregate) for the original
     * signature.
     * 
     * @author Gabor Bergmann
     */
    protected class AggregatorOuterIndexer extends StandardIndexer {

        public AggregatorOuterIndexer() {
            super(AbstractColumnAggregatorNode.this.reteContainer, TupleMask.omit(sourceWidth, sourceWidth + 1));
            this.parent = AbstractColumnAggregatorNode.this;
        }

        @Override
        public Collection<Tuple> get(final Tuple signature) {
            final Tuple aggregateTuple = getAggregateTuple(signature);
            return aggregateTuple == null ? null : Collections.singleton(aggregateTuple);
        }

        public void propagate(final Tuple signature, final Tuple oldTuple, final Tuple newTuple,
                final Timestamp timestamp) {
            if (oldTuple != null) {
                propagate(Direction.REVOKE, oldTuple, signature, true, timestamp);
            }
            if (newTuple != null) {
                propagate(Direction.INSERT, newTuple, signature, true, timestamp);
            }
        }

        @Override
        public Node getActiveNode() {
            return AbstractColumnAggregatorNode.this;
        }

    }

    /**
     * A special non-iterable index that checks a suspected aggregate value for a given signature. The signature for
     * this index is the original 'group by' masked tuple, with the suspected result inserted at position
     * resultPositionInSignature.
     * 
     * @author Gabor Bergmann
     */
    protected class AggregatorOuterIdentityIndexer extends StandardIndexer {
        protected final int resultPositionInSignature;
        protected final TupleMask pruneResult;
        protected final TupleMask reorderMask;

        public AggregatorOuterIdentityIndexer(final int resultPositionInSignature) {
            super(AbstractColumnAggregatorNode.this.reteContainer,
                    TupleMask.displace(sourceWidth, resultPositionInSignature, sourceWidth + 1));
            this.parent = AbstractColumnAggregatorNode.this;
            this.resultPositionInSignature = resultPositionInSignature;
            this.pruneResult = TupleMask.omit(resultPositionInSignature, sourceWidth + 1);
            if (resultPositionInSignature == sourceWidth) {
                this.reorderMask = null;
            } else {
                this.reorderMask = mask;
            }
        }

        @Override
        public Collection<Tuple> get(final Tuple signatureWithResult) {
            final Tuple prunedSignature = pruneResult.transform(signatureWithResult);
            final AggregateResult result = getAggregateResult(prunedSignature);
            if (result != null && Objects.equals(signatureWithResult.get(resultPositionInSignature), result)) {
                return Collections.singleton(signatureWithResult);
            } else {
                return null;
            }
        }

        public void propagate(final Tuple signature, final Tuple oldTuple, final Tuple newTuple,
                final Timestamp timestamp) {
            if (oldTuple != null) {
                propagate(Direction.REVOKE, reorder(oldTuple), signature, true, timestamp);
            }
            if (newTuple != null) {
                propagate(Direction.INSERT, reorder(newTuple), signature, true, timestamp);
            }
        }

        private Tuple reorder(final Tuple signatureWithResult) {
            Tuple transformed;
            if (reorderMask == null) {
                transformed = signatureWithResult;
            } else {
                transformed = reorderMask.transform(signatureWithResult);
            }
            return transformed;
        }

        @Override
        public Node getActiveNode() {
            return AbstractColumnAggregatorNode.this;
        }
    }

}
