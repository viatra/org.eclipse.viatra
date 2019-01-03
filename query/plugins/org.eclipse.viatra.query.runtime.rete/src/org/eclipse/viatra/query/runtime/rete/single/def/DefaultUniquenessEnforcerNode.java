/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *    Tamás Szabó - delete and rederive algorithm implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.single.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiset;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.index.SpecializedProjectionIndexer.ListenerSubscription;
import org.eclipse.viatra.query.runtime.rete.index.def.DefaultMemoryIdentityIndexer;
import org.eclipse.viatra.query.runtime.rete.index.def.DefaultMemoryNullIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.PosetAwareReceiver;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.ddf.DifferentialMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.def.PosetAwareMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.def.ShapeshifterMailbox;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;
import org.eclipse.viatra.query.runtime.rete.util.Options;

/**
 * Ensures that no identical copies get to the output. Only one replica of each pattern substitution may traverse this
 * node.
 * 
 * The node is capable of operating in the delete and re-derive mode. In this mode, it is also possible to equip the
 * node with an {@link IPosetComparator} to identify monotone changes; thus, ensuring that a fix-point can be reached
 * during the evaluation.
 * 
 * @author Gabor Bergmann
 * @author Tamas Szabo
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients. 
 */
public class DefaultUniquenessEnforcerNode extends StandardNode
        implements Tunnel, RederivableNode, PosetAwareReceiver {

    protected Collection<Supplier> parents;
    protected IMultiset<Tuple> memory;
    /**
     * @since 1.6
     */
    protected IMultiset<Tuple> rederivableMemory;
    /**
     * @since 1.6
     */
    protected boolean deleteRederiveEvaluation;
    protected ProjectionIndexer memoryNullIndexer;
    protected ProjectionIndexer memoryIdentityIndexer;
    protected final int tupleWidth;
    /**
     * @since 1.6
     */
    protected final Mailbox mailbox;
    private final TupleMask nullMask;
    private final TupleMask identityMask;
    
    /**
     * @since 1.7
     */
    protected final List<ListenerSubscription> specializedListeners;
    /**
     * @since 1.7
     */
    protected CommunicationGroup currentGroup = null;

    public DefaultUniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth) {
        this(reteContainer, tupleWidth, false);
    }
    
    /**
     * OPTIONAL ELEMENT - ONLY PRESENT IF MONOTONICITY INFO WAS AVAILABLE
     * @since 1.6
     */
    protected final TupleMask coreMask;
    /**
     * OPTIONAL ELEMENTS - ONLY PRESENT IF MONOTONICITY INFO WAS AVAILABLE
     * @since 1.6
     */
    protected final TupleMask posetMask;
    /**
     * OPTIONAL ELEMENTS - ONLY PRESENT IF MONOTONICITY INFO WAS AVAILABLE
     * @since 1.6
     */
    protected final IPosetComparator posetComparator;

    /**
     * @since 1.6
     */
    public DefaultUniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth, boolean deleteRederiveEvaluation) {
        this(reteContainer, tupleWidth, deleteRederiveEvaluation, null, null, null);
    }

    /**
     * @since 1.6
     */
    public DefaultUniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth, boolean deleteRederiveEvaluation,
            TupleMask coreMask, TupleMask posetMask, IPosetComparator posetComparator) {
        super(reteContainer);
        this.parents = new ArrayList<Supplier>();
        this.specializedListeners = new ArrayList<ListenerSubscription>();
        this.memory = CollectionsFactory.createMultiset();
        this.rederivableMemory = CollectionsFactory.createMultiset();
        this.tupleWidth = tupleWidth;
        reteContainer.registerClearable(this.memory);
        reteContainer.registerClearable(this.rederivableMemory);
        this.nullMask = TupleMask.linear(0, tupleWidth);
        this.identityMask = TupleMask.identity(tupleWidth);
        this.deleteRederiveEvaluation = deleteRederiveEvaluation;
        this.coreMask = coreMask;
        this.posetMask = posetMask;
        this.posetComparator = posetComparator;
        this.mailbox = instantiateMailbox();
        reteContainer.registerClearable(this.mailbox);
    }
    
    @Override
    public boolean isInDRedMode() {
        return this.deleteRederiveEvaluation;
    }

    @Override
    public TupleMask getCoreMask() {
        return coreMask;
    }

    @Override
    public TupleMask getPosetMask() {
        return posetMask;
    }

    @Override
    public IPosetComparator getPosetComparator() {
        return posetComparator;
    }
    
    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector, final boolean flush) {
        throw new UnsupportedOperationException("Use the timely version of this node!");
    }

    /**
     * @since 2.0
     */
    protected Mailbox instantiateMailbox() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()) {
            return new DifferentialMailbox(this, this.reteContainer);
        } else if (coreMask != null && posetMask != null && posetComparator != null) {
            return new PosetAwareMailbox(this, this.reteContainer);
        } else {
            return new ShapeshifterMailbox(this, this.reteContainer);
        }
    }

    /**
     * @since 1.7
     */
    public IMultiset<Tuple> getMemory() {
        return memory;
    }

    @Override
    public Mailbox getMailbox() {
        return this.mailbox;
    }

    @Override
    public void update(Direction direction, Tuple update, DifferentialTimestamp timestamp) {
        updateWithPosetInfo(direction, update, false, timestamp);
    }

    @Override
    public void updateWithPosetInfo(Direction direction, Tuple update, boolean monotone, DifferentialTimestamp timestamp) {
        if (this.deleteRederiveEvaluation) {
            if (updateWithDeleteAndRederive(direction, update, monotone)) {
                propagate(direction, update, timestamp);
            }
        } else {
            if (updateDefault(direction, update)) {
                propagate(direction, update, timestamp);
            }
        }
    }

    /**
     * @since 1.6
     */
    protected boolean updateWithDeleteAndRederive(Direction direction, Tuple update, boolean monotone) {
        boolean propagate = false;

        int memoryCount = memory.getCount(update);
        int rederivableCount = rederivableMemory.getCount(update);

        if (direction == Direction.INSERT) {
            // INSERT
            if (rederivableCount != 0) {
                // the tuple is in the re-derivable memory
                rederivableMemory.addOne(update);
                if (rederivableMemory.isEmpty()) {
                    // there is nothing left to be re-derived
                    // this can happen if the INSERT cancelled out a DELETE
                    currentGroup.removeRederivable(this);
                }
            } else {
                // the tuple is in the main memory
                propagate = memory.addOne(update);
            }
        } else {
            // DELETE
            if (rederivableCount != 0) {
                // the tuple is in the re-derivable memory
                if (memoryCount != 0) {
                    issueError("[INTERNAL ERROR] Inconsistent state for " + update
                            + " because it is present both in the main and re-derivable memory in the UniquenessEnforcerNode "
                            + this + " for pattern(s) " + getTraceInfoPatternsEnumerated(), null);
                }

                try {
                    rederivableMemory.removeOne(update);
                } catch (IllegalStateException ex) {
                    issueError(
                            "[INTERNAL ERROR] Duplicate deletion of " + update + " was detected in UniquenessEnforcer "
                                    + this + " for pattern(s) " + getTraceInfoPatternsEnumerated(),
                            ex);
                }
                if (rederivableMemory.isEmpty()) {
                    // there is nothing left to be re-derived
                    currentGroup.removeRederivable(this);
                }
            } else {
                // the tuple is in the main memory
                if (monotone) {
                    propagate = memory.removeOne(update);
                } else {
                    int count = memoryCount - 1;
                    if (count > 0) {
                        if (rederivableMemory.isEmpty()) {
                            // there is now something to be re-derived
                            currentGroup.addRederivable(this);
                        }
                        rederivableMemory.addPositive(update, count);
                    }
                    memory.clearAllOf(update);
                    propagate = true;
                }
            }
        }

        return propagate;
    }

    /**
     * @since 1.6
     */
    protected boolean updateDefault(Direction direction, Tuple update) {
        boolean propagate = false;
        if (direction == Direction.INSERT) {
            // INSERT
            propagate = memory.addOne(update);
        } else {
            // DELETE
            try {
                propagate = memory.removeOne(update);
            } catch (IllegalStateException ex) {
                propagate = false;
                issueError("[INTERNAL ERROR] Duplicate deletion of " + update + " was detected in " + this.getClass().getName() + " "
                        + this + " for pattern(s) " + getTraceInfoPatternsEnumerated(), ex);
            }
        }
        return propagate;
    }

    /**
     * @since 1.6
     */
    @Override
    public void rederiveOne() {
        Tuple update = rederivableMemory.iterator().next();
        int count = rederivableMemory.getCount(update);
        rederivableMemory.clearAllOf(update);
        memory.addPositive(update, count);
        // if there is no other re-derivable tuple, then unregister the node itself
        if (this.rederivableMemory.isEmpty()) {
            currentGroup.removeRederivable(this);
        }
        propagate(Direction.INSERT, update, DifferentialTimestamp.ZERO);
    }
    
    /**
     * @since 1.6
     */
    protected void propagate(Direction direction, Tuple update, DifferentialTimestamp timestamp) {
        // See Bug 518434
        // trivial (non-active) indexers must be updated before other listeners
        // so that if they are joined against each other, trivial indexers lookups 
        // will be consistent with their notifications;
        // also, their subscriptions must share a single order
        for (ListenerSubscription subscription : specializedListeners) {
            subscription.propagate(direction, update, timestamp);
        }
        
        propagateUpdate(direction, update, timestamp);
    }

    @Override
    public ProjectionIndexer constructIndex(TupleMask mask, TraceInfo... traces) {
        if (Options.employTrivialIndexers) {
            if (nullMask.equals(mask)) {
                final ProjectionIndexer indexer = getNullIndexer();
                for (TraceInfo traceInfo : traces)
                    indexer.assignTraceInfo(traceInfo);
                return indexer;
            }
            if (identityMask.equals(mask)) {
                final ProjectionIndexer indexer = getIdentityIndexer();
                for (TraceInfo traceInfo : traces)
                    indexer.assignTraceInfo(traceInfo);
                return indexer;
            }
        }
        return super.constructIndex(mask, traces);
    }

    @Override
    public void pullInto(Collection<Tuple> collector, boolean flush) {
        for (Tuple t : memory) 
            collector.add(t);
    }

    public ProjectionIndexer getNullIndexer() {
        if (memoryNullIndexer == null) {
            memoryNullIndexer = new DefaultMemoryNullIndexer(reteContainer, tupleWidth, memory.distinctValues(), this, this, specializedListeners);
            this.getCommunicationTracker().registerDependency(this, memoryNullIndexer);
        }
        return memoryNullIndexer;
    }

    public ProjectionIndexer getIdentityIndexer() {
        if (memoryIdentityIndexer == null) {
            memoryIdentityIndexer = new DefaultMemoryIdentityIndexer(reteContainer, tupleWidth, memory.distinctValues(), this, this, specializedListeners);
            this.getCommunicationTracker().registerDependency(this, memoryIdentityIndexer);
        }
        return memoryIdentityIndexer;
    }

    @Override
    public void appendParent(Supplier supplier) {
        parents.add(supplier);
    }

    @Override
    public void removeParent(Supplier supplier) {
        parents.remove(supplier);
    }

    @Override
    public Collection<Supplier> getParents() {
        return parents;
    }

    @Override
    public void assignTraceInfo(TraceInfo traceInfo) {
        super.assignTraceInfo(traceInfo);
        if (traceInfo.propagateFromStandardNodeToSupplierParent())
            for (Supplier parent : parents)
                parent.acceptPropagatedTraceInfo(traceInfo);
    }

    public CommunicationGroup getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(CommunicationGroup currentGroup) {
        this.currentGroup = currentGroup;
    }

}
