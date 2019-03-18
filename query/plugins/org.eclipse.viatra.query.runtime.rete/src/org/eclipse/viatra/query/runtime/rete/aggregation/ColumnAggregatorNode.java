/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
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
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.index.StandardIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.PosetAwareReceiver;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.ddf.DifferentialMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.def.PosetAwareMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.def.ShapeshifterMailbox;
import org.eclipse.viatra.query.runtime.rete.single.SingleInputNode;

/**
 * Groups incoming tuples by the given mask, and aggregates values at a specific index in each group.
 * 
 * <p>
 * Direct children are not supported, use via outer join indexers instead.
 *
 * <p>
 * 
 * This node cannot be used in recursive differential dataflow evaluation.
 * 
 * @author Gabor Bergmann
 * @author Tamas Szabo
 * @since 1.4
 */
public class ColumnAggregatorNode<Domain, Accumulator, AggregateResult> extends SingleInputNode
        implements Clearable, IAggregatorNode, RederivableNode, PosetAwareReceiver {

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
    protected final IPosetComparator posetComparator;
    /**
     * @since 1.6
     */
    protected final int sourceWidth;
    /**
     * @since 1.6
     */
    protected final IQueryRuntimeContext runtimeContext;
    /**
     * @since 1.6
     */
    protected final boolean deleteRederiveEvaluation;

    // invariant: neutral values are not stored
    /**
     * @since 1.6
     */
    protected final Map<Tuple, Accumulator> memory;
    /**
     * @since 1.6
     */
    protected final Map<Tuple, Accumulator> rederivableMemory;

    /**
     * @since 1.7
     */
    protected CommunicationGroup currentGroup = null;

    private final AggregateResult NEUTRAL;

    AggregatorOuterIndexer aggregatorOuterIndexer = null;
    @SuppressWarnings("rawtypes")
    ColumnAggregatorNode.AggregatorOuterIdentityIndexer[] aggregatorOuterIdentityIndexers = null;

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
    public ColumnAggregatorNode(ReteContainer reteContainer,
            IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator,
            boolean deleteRederiveEvaluation, TupleMask groupMask, TupleMask columnMask,
            IPosetComparator posetComparator) {
        super(reteContainer);
        this.operator = operator;
        this.groupMask = groupMask;
        this.columnMask = columnMask;
        this.memory = CollectionsFactory.createMap();
        this.rederivableMemory = CollectionsFactory.createMap();
        this.deleteRederiveEvaluation = deleteRederiveEvaluation;
        this.posetComparator = posetComparator;
        this.mailbox = instantiateMailbox();
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
    public ColumnAggregatorNode(ReteContainer reteContainer,
            IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator, TupleMask groupMask,
            int aggregatedColumn) {
        this(reteContainer, operator, false, groupMask, TupleMask.selectSingle(aggregatedColumn, groupMask.sourceWidth),
                null);
    }
    
    @Override
    public void networkStructureChanged() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation() && this.reteContainer.getCommunicationTracker().isInRecursiveGroup(this)) {
            throw new IllegalStateException(this.toString() + " cannot be used in recursive differential dataflow evaluation!");
        }
        super.networkStructureChanged();
    }
    
    @Override
    public CommunicationTracker getCommunicationTracker() {
        return this.reteContainer.getCommunicationTracker();
    }

    @Override
    public boolean isInDRedMode() {
        return this.deleteRederiveEvaluation;
    }

    @Override
    protected Mailbox instantiateMailbox() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()) {
            return new DifferentialMailbox(this, this.reteContainer);
        } else if (groupMask != null && columnMask != null && posetComparator != null) {
            return new PosetAwareMailbox(this, this.reteContainer);
        } else {
            return new ShapeshifterMailbox(this, this.reteContainer);
        }
    }

    @Override
    public TupleMask getCoreMask() {
        return groupMask;
    }

    @Override
    public TupleMask getPosetMask() {
        return columnMask;
    }

    @Override
    public IPosetComparator getPosetComparator() {
        return posetComparator;
    }

    @Override
    public void pullInto(Collection<Tuple> collector, boolean flush) {
        // DIRECT CHILDREN NOT SUPPORTED
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void pullIntoWithTimestamp(Map<Tuple, DifferentialTimestamp> collector, boolean flush) {
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
    public Indexer getAggregatorOuterIdentityIndexer(int resultPositionInSignature) {
        if (aggregatorOuterIdentityIndexers == null)
            aggregatorOuterIdentityIndexers = new ColumnAggregatorNode.AggregatorOuterIdentityIndexer[sourceWidth + 1];
        if (aggregatorOuterIdentityIndexers[resultPositionInSignature] == null) {
            aggregatorOuterIdentityIndexers[resultPositionInSignature] = new AggregatorOuterIdentityIndexer(
                    resultPositionInSignature);
            this.getCommunicationTracker().registerDependency(this, aggregatorOuterIdentityIndexers[resultPositionInSignature]);
        }
        return aggregatorOuterIdentityIndexers[resultPositionInSignature];
    }

    @Override
    public void rederiveOne() {
        Entry<Tuple, Accumulator> entry = rederivableMemory.entrySet().iterator().next();
        Tuple group = entry.getKey();
        Accumulator accumulator = entry.getValue();
        rederivableMemory.remove(group);
        memory.put(group, accumulator);
        // unregister the node if there is nothing left to be re-derived
        if (this.rederivableMemory.isEmpty()) {
            currentGroup.removeRederivable(this);
        }
        AggregateResult value = operator.getAggregate(accumulator);
        propagate(group, NEUTRAL, value, null);
    }

    @Override
    public void updateWithPosetInfo(Direction direction, Tuple update, boolean monotone, DifferentialTimestamp timestamp) {
        if (this.deleteRederiveEvaluation) {
            updateWithDeleteAndRederive(direction, update, monotone);
        } else {
            updateDefault(direction, update, timestamp);
        }
    }

    @Override
    public void update(Direction direction, Tuple update, DifferentialTimestamp timestamp) {
        updateWithPosetInfo(direction, update, false, timestamp);
    }

    /**
     * @since 1.6
     */
    protected void updateDefault(Direction direction, Tuple update, DifferentialTimestamp timestamp) {
        final Tuple key = groupMask.transform(update);
        final Tuple value = columnMask.transform(update);
        @SuppressWarnings("unchecked")
        final Domain aggregableValue = (Domain) runtimeContext.unwrapElement(value.get(0));
        final boolean isInsertion = direction == Direction.INSERT;

        final Accumulator oldMainAccumulator = getMainAccumulator(key);
        final AggregateResult oldValue = operator.getAggregate(oldMainAccumulator);

        final Accumulator newMainAccumulator = operator.update(oldMainAccumulator, aggregableValue, isInsertion);
        storeIfNotNeutral(key, newMainAccumulator, memory);
        final AggregateResult newValue = operator.getAggregate(newMainAccumulator);

        propagate(key, oldValue, newValue, timestamp);
    }

    /**
     * @since 1.6
     */
    protected void updateWithDeleteAndRederive(Direction direction, Tuple update, boolean monotone) {
        final Tuple group = groupMask.transform(update);
        final Tuple value = columnMask.transform(update);
        @SuppressWarnings("unchecked")
        final Domain aggregableValue = (Domain) runtimeContext.unwrapElement(value.get(0));
        final boolean isInsertion = direction == Direction.INSERT;

        Accumulator oldMainAccumulator = memory.get(group);
        Accumulator oldRederivableAccumulator = rederivableMemory.get(group);

        if (direction == Direction.INSERT) {
            // INSERT
            if (oldRederivableAccumulator != null) {
                // the group is in the re-derivable memory
                Accumulator newRederivableAccumulator = operator.update(oldRederivableAccumulator, aggregableValue,
                        isInsertion);
                storeIfNotNeutral(group, newRederivableAccumulator, rederivableMemory);
                if (rederivableMemory.isEmpty()) {
                    // there is nothing left to be re-derived
                    // this can happen if the accumulator became neutral in response to the INSERT
                    currentGroup.removeRederivable(this);
                }
            } else {
                // the group is in the main memory

                // at this point, it can happen that we need to initialize with a neutral accumulator
                if (oldMainAccumulator == null) {
                    oldMainAccumulator = operator.createNeutral();
                }

                AggregateResult oldValue = operator.getAggregate(oldMainAccumulator);
                Accumulator newMainAccumulator = operator.update(oldMainAccumulator, aggregableValue, isInsertion);
                storeIfNotNeutral(group, newMainAccumulator, memory);
                AggregateResult newValue = operator.getAggregate(newMainAccumulator);
                propagate(group, oldValue, newValue, null);
            }
        } else {
            // DELETE
            if (oldRederivableAccumulator != null) {
                // the group is in the re-derivable memory
                if (oldMainAccumulator != null) {
                    issueError("[INTERNAL ERROR] Inconsistent state for " + update
                            + " because it is present both in the main and re-derivable memory in the ColumnAggregatorNode "
                            + this + " for pattern(s) " + getTraceInfoPatternsEnumerated(), null);
                }
                try {
                    Accumulator newRederivableAccumulator = operator.update(oldRederivableAccumulator, aggregableValue,
                            isInsertion);
                    storeIfNotNeutral(group, newRederivableAccumulator, rederivableMemory);
                    if (rederivableMemory.isEmpty()) {
                        // there is nothing left to be re-derived
                        // this can happen if the accumulator became neutral in response to the DELETE
                        currentGroup.removeRederivable(this);
                    }
                } catch (NullPointerException ex) {
                    issueError("[INTERNAL ERROR] Deleting a domain element in " + update
                            + " which did not exist before in ColumnAggregatorNode " + this + " for pattern(s) "
                            + getTraceInfoPatternsEnumerated(), ex);
                }
            } else {
                // the group is in the main memory
                // at this point, it can happen that we need to initialize with a neutral accumulator
                if (oldMainAccumulator == null) {
                    oldMainAccumulator = operator.createNeutral();
                }

                AggregateResult oldValue = operator.getAggregate(oldMainAccumulator);
                Accumulator newMainAccumulator = operator.update(oldMainAccumulator, aggregableValue, isInsertion);
                AggregateResult newValue = operator.getAggregate(newMainAccumulator);

                if (monotone) {
                    storeIfNotNeutral(group, newMainAccumulator, memory);
                    propagate(group, oldValue, newValue, null);
                } else {
                    boolean wasEmpty = rederivableMemory.isEmpty();
                    if (storeIfNotNeutral(group, newMainAccumulator, rederivableMemory) && wasEmpty) {
                        currentGroup.addRederivable(this);
                    }
                    memory.remove(group);
                    propagate(group, oldValue, NEUTRAL, null);
                }
            }
        }
    }

    /**
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public void propagate(Tuple group, AggregateResult oldValue, AggregateResult newValue, DifferentialTimestamp timestamp) {
        if (!Objects.equals(oldValue, newValue)) {
            Tuple oldResultTuple = tupleFromAggregateResult(group, oldValue);
            Tuple newResultTuple = tupleFromAggregateResult(group, newValue);

            if (aggregatorOuterIndexer != null) {
                aggregatorOuterIndexer.propagate(group, oldResultTuple, newResultTuple, timestamp);
            }
            if (aggregatorOuterIdentityIndexers != null) {
                for (AggregatorOuterIdentityIndexer aggregatorOuterIdentityIndexer : aggregatorOuterIdentityIndexers) {
                    if (aggregatorOuterIdentityIndexer != null) {
                        aggregatorOuterIdentityIndexer.propagate(group, oldResultTuple, newResultTuple, timestamp);
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        memory.clear();
        rederivableMemory.clear();
    }

    /**
     * Returns true if the accumulator was stored, false otherwise.
     * 
     * @since 1.6
     */
    protected boolean storeIfNotNeutral(Tuple key, Accumulator accumulator, Map<Tuple, Accumulator> memory) {
        if (operator.isNeutral(accumulator)) {
            memory.remove(key);
            return false;
        } else {
            memory.put(key, accumulator);
            return true;
        }
    }

    public Tuple getAggregateTuple(Tuple key) {
        Accumulator accumulator = getMainAccumulator(key);
        AggregateResult aggregateResult = operator.getAggregate(accumulator);
        return tupleFromAggregateResult(key, aggregateResult);
    }

    public AggregateResult getAggregateResult(Tuple key) {
        Accumulator accumulator = getMainAccumulator(key);
        return operator.getAggregate(accumulator);
    }

    /**
     * @since 1.6
     */
    protected Accumulator getMainAccumulator(Tuple key) {
        return getAccumulator(key, memory);
    }

    /**
     * @since 1.6
     */
    protected Accumulator getRederivableAccumulator(Tuple key) {
        return getAccumulator(key, rederivableMemory);
    }

    /**
     * @since 1.6
     */
    protected Accumulator getAccumulator(Tuple key, Map<Tuple, Accumulator> memory) {
        Accumulator accumulator = memory.get(key);
        if (accumulator == null) {
            accumulator = operator.createNeutral();
        }
        return accumulator;
    }

    protected Tuple tupleFromAggregateResult(Tuple groupTuple, AggregateResult aggregateResult) {
        if (aggregateResult == null)
            return null;
        return Tuples.staticArityLeftInheritanceTupleOf(groupTuple, runtimeContext.wrapElement(aggregateResult));
    }

    @Override
    public CommunicationGroup getCurrentGroup() {
        return currentGroup;
    }

    @Override
    public void setCurrentGroup(CommunicationGroup currentGroup) {
        this.currentGroup = currentGroup;
    }

    /**
     * A special non-iterable index that retrieves the aggregated, packed result (signature+aggregate) for the original
     * signature.
     * 
     * @author Gabor Bergmann
     */
    class AggregatorOuterIndexer extends StandardIndexer {

        public AggregatorOuterIndexer() {
            super(ColumnAggregatorNode.this.reteContainer, TupleMask.omit(sourceWidth, sourceWidth + 1));
            this.parent = ColumnAggregatorNode.this;
        }

        @Override
        public Collection<Tuple> get(Tuple signature) {
            Tuple aggregateTuple = getAggregateTuple(signature);
            return aggregateTuple == null ? null : Collections.singleton(aggregateTuple);
        }

        public void propagate(Tuple signature, Tuple oldTuple, Tuple newTuple, DifferentialTimestamp timestamp) {
            if (oldTuple != null)
                propagate(Direction.REVOKE, oldTuple, signature, true, timestamp);
            if (newTuple != null)
                propagate(Direction.INSERT, newTuple, signature, true, timestamp);
        }

        @Override
        public Node getActiveNode() {
            return ColumnAggregatorNode.this;
        }

    }

    /**
     * A special non-iterable index that checks a suspected aggregate value for a given signature. The signature for
     * this index is the original 'group by' masked tuple, with the suspected result inserted at position
     * resultPositionInSignature.
     * 
     * @author Gabor Bergmann
     */

    class AggregatorOuterIdentityIndexer extends StandardIndexer /* implements Receiver */ {
        int resultPositionInSignature;
        TupleMask pruneResult;
        TupleMask reorderMask;

        public AggregatorOuterIdentityIndexer(int resultPositionInSignature) {
            super(ColumnAggregatorNode.this.reteContainer,
                    TupleMask.displace(sourceWidth, resultPositionInSignature, sourceWidth + 1));
            this.parent = ColumnAggregatorNode.this;
            this.resultPositionInSignature = resultPositionInSignature;
            this.pruneResult = TupleMask.omit(resultPositionInSignature, sourceWidth + 1);
            if (resultPositionInSignature == sourceWidth)
                this.reorderMask = null;
            else
                this.reorderMask = mask;
        }

        @Override
        public Collection<Tuple> get(Tuple signatureWithResult) {
            Tuple prunedSignature = pruneResult.transform(signatureWithResult);
            AggregateResult result = getAggregateResult(prunedSignature);
            if (result != null && Objects.equals(signatureWithResult.get(resultPositionInSignature), result))
                return Collections.singleton(signatureWithResult);
            else
                return null;
        }

        public void propagate(Tuple signature, Tuple oldTuple, Tuple newTuple, DifferentialTimestamp timestamp) {
            if (oldTuple != null)
                propagate(Direction.REVOKE, reorder(oldTuple), signature, true, timestamp);
            if (newTuple != null)
                propagate(Direction.INSERT, reorder(newTuple), signature, true, timestamp);
        }

        private Tuple reorder(Tuple signatureWithResult) {
            Tuple transformed;
            if (reorderMask == null)
                transformed = signatureWithResult;
            else
                transformed = reorderMask.transform(signatureWithResult);
            return transformed;
        }

        @Override
        public Node getActiveNode() {
            return ColumnAggregatorNode.this;
        }
    }

}
