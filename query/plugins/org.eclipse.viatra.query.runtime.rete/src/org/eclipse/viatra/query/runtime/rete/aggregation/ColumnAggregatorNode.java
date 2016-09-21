/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.tuple.LeftInheritanceTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.index.StandardIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.single.SingleInputNode;
import org.eclipse.viatra.query.runtime.rete.tuple.Clearable;

/**
 * Groups incoming tuples by the given mask, and aggregates values at a specific index in each group.
 * 
 * <p>
 * Direct children are nor supported, use via outer join indexers instead.
 * 
 * <p>
 * 
 * @author Gabor Bergmann
 * @since 1.4
 */
public class ColumnAggregatorNode<Domain, Accumulator, AggregateResult> extends SingleInputNode
        implements Clearable, IAggregatorNode {

    private IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator;
    private TupleMask groupByMask;
    private int aggregableColumnIndex;
    private int sourceWidth;
    private IQueryRuntimeContext runtimeContext;

    // invariant: neutral values are not stored
    Map<Tuple, Accumulator> accumulatorsByGroup = CollectionsFactory.getMap();

    AggregatorOuterIndexer aggregatorOuterIndexer = null;
    ColumnAggregatorNode.AggregatorOuterIdentityIndexer[] aggregatorOuterIdentityIndexers = null;

    /**
     * @param reteContainer
     */
    public ColumnAggregatorNode(ReteContainer reteContainer,
            IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator, TupleMask groupByMask,
            int aggregableColumnIndex) {
        super(reteContainer);
        this.operator = operator;
        this.groupByMask = groupByMask;
        this.aggregableColumnIndex = aggregableColumnIndex;

        sourceWidth = groupByMask.indices.length;
        runtimeContext = reteContainer.getNetwork().getEngine().getRuntimeContext();
        reteContainer.registerClearable(this);
    }

    @Override
    public void pullInto(Collection<Tuple> collector) {
        // DIRECT CHILDREN NOT SUPPORTED
        throw new UnsupportedOperationException();
        // for (Entry<Tuple, Accumulator> group : accumulatorsByGroup.entrySet()) {
        // tupleFromAccumulator(groupTuple, accumulator)
        // }
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
            // reteContainer.connectAndSynchronize(this, aggregatorOuterIndexer);
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
            // reteContainer.connectAndSynchronize(this, aggregatorOuterIdentityIndexers[resultPositionInSignature]);
        }
        return aggregatorOuterIdentityIndexers[resultPositionInSignature];
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
        Tuple updateGroup = groupByMask.transform(updateElement);

        Accumulator oldAccumulator = getCurrentAccumulator(updateGroup);
        AggregateResult oldAggregateResult = operator.getAggregate(oldAccumulator);

        Domain aggregableValue = (Domain) runtimeContext.unwrapElement(updateElement.get(aggregableColumnIndex));

        boolean isInsertion = direction == Direction.INSERT;
        Accumulator newAccumulator = operator.update(oldAccumulator, aggregableValue, isInsertion);

        if (operator.isNeutral(newAccumulator))
            accumulatorsByGroup.remove(updateGroup);
        else
            accumulatorsByGroup.put(updateGroup, newAccumulator);

        AggregateResult newAggregateResult = operator.getAggregate(newAccumulator);

        if (Objects.equals(oldAggregateResult, newAggregateResult)) {
            // no actual difference in aggregates, no need to propagate
            return;
        } else {
            Tuple oldResultTuple = tupleFromAggregateResult(updateGroup, oldAggregateResult);
            Tuple newResultTuple = tupleFromAggregateResult(updateGroup, newAggregateResult);

            // No direct children to notify!
            // if (oldResultTuple != null) propagateUpdate(Direction.REVOKE, oldResultTuple);
            // if (newResultTuple != null) propagateUpdate(Direction.INSERT, newResultTuple);

            if (aggregatorOuterIndexer != null)
                aggregatorOuterIndexer.propagate(updateGroup, oldResultTuple, newResultTuple);
            if (aggregatorOuterIdentityIndexers != null)
                for (AggregatorOuterIdentityIndexer aggregatorOuterIdentityIndexer : aggregatorOuterIdentityIndexers)
                    if (aggregatorOuterIdentityIndexer != null)
                        aggregatorOuterIdentityIndexer.propagate(updateGroup, oldResultTuple, newResultTuple);
        }

    }

    @Override
    public void clear() {
        accumulatorsByGroup.clear();
    }

    public Tuple getAggregateTuple(Tuple groupTuple) {
        Accumulator accumulator = getCurrentAccumulator(groupTuple);
        AggregateResult aggregateResult = operator.getAggregate(accumulator);
        return tupleFromAggregateResult(groupTuple, aggregateResult);
    }

    public AggregateResult getAggregateResult(Tuple groupTuple) {
        Accumulator accumulator = getCurrentAccumulator(groupTuple);
        return operator.getAggregate(accumulator);
    }

    private Accumulator getCurrentAccumulator(Tuple groupTuple) {
        Accumulator accumulator = accumulatorsByGroup.get(groupTuple);
        if (accumulator == null)
            accumulator = operator.createNeutral();
        return accumulator;
    }

    // protected Tuple tupleFromAccumulator(Tuple groupTuple, Accumulator accumulator) {
    // return tupleFromAggregateResult(groupTuple, aggregateResult);
    // }
    protected Tuple tupleFromAggregateResult(Tuple groupTuple, AggregateResult aggregateResult) {
        if (aggregateResult == null)
            return null;
        Object[] resultArray = { runtimeContext.wrapElement(aggregateResult) };
        return new LeftInheritanceTuple(groupTuple, resultArray);
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

        public void propagate(Tuple signature, Tuple oldTuple, Tuple newTuple) {
            if (oldTuple != null)
                propagate(Direction.REVOKE, oldTuple, signature, true);
            if (newTuple != null)
                propagate(Direction.INSERT, newTuple, signature, true);
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

        public void propagate(Tuple signature, Tuple oldTuple, Tuple newTuple) {
            if (oldTuple != null)
                propagate(Direction.REVOKE, reorder(oldTuple), signature, true);
            if (newTuple != null)
                propagate(Direction.INSERT, reorder(newTuple), signature, true);
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
