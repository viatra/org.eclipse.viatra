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

package org.eclipse.viatra.query.runtime.rete.single;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiset;
import org.eclipse.viatra.query.runtime.rete.index.MemoryIdentityIndexer;
import org.eclipse.viatra.query.runtime.rete.index.MemoryNullIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.index.SpecializedProjectionIndexer.ListenerSubscription;
import org.eclipse.viatra.query.runtime.rete.network.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.DefaultMailbox;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.MonotonicityAwareMailbox;
import org.eclipse.viatra.query.runtime.rete.network.MonotonicityAwareReceiver;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
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
public class UniquenessEnforcerNode extends StandardNode
        implements Tunnel, RederivableNode, MonotonicityAwareReceiver {

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
    protected MemoryNullIndexer memoryNullIndexer;
    protected MemoryIdentityIndexer memoryIdentityIndexer;
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

    public UniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth) {
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
    public UniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth, boolean deleteRederiveEvaluation) {
        this(reteContainer, tupleWidth, deleteRederiveEvaluation, null, null, null);
    }

    /**
     * @since 1.6
     */
    public UniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth, boolean deleteRederiveEvaluation,
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

    /**
     * @since 1.6
     */
    protected Mailbox instantiateMailbox() {
        if (coreMask != null && posetMask != null && posetComparator != null) {
            return new MonotonicityAwareMailbox(this, this.reteContainer);
        } else {
            return new DefaultMailbox(this, this.reteContainer);
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
        return mailbox;
    }

    @Override
    public void update(Direction direction, Tuple update) {
        update(direction, update, false);
    }

    @Override
    public void update(Direction direction, Tuple update, boolean monotone) {
        if (this.deleteRederiveEvaluation) {
            if (updateWithDeleteAndRederive(direction, update, monotone)) {
                propagate(direction, update);
            }
        } else {
            if (updateDefault(direction, update)) {
                propagate(direction, update);
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
                issueError("[INTERNAL ERROR] Duplicate deletion of " + update + " was detected in UniquenessEnforcer "
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
        propagate(Direction.INSERT, update);
    }

    /**
     * @since 1.6
     */
    protected void propagate(Direction direction, Tuple update) {
        // See Bug 518434
        // trivial (non-active) indexers must be updated before other listeners
        // so that if they are joined against each other, trivial indexers lookups 
        // will be consistent with their notifications;
        // also, their subscriptions must share a single order
        for (ListenerSubscription subscription : specializedListeners) {
            subscription.propagate(direction, update);
        }
        
        propagateUpdate(direction, update);
    }

    @Override
    public ProjectionIndexer constructIndex(TupleMask mask, TraceInfo... traces) {
        if (Options.employTrivialIndexers) {
            if (nullMask.equals(mask)) {
                final MemoryNullIndexer indexer = getNullIndexer();
                for (TraceInfo traceInfo : traces)
                    indexer.assignTraceInfo(traceInfo);
                return indexer;
            }
            if (identityMask.equals(mask)) {
                final MemoryIdentityIndexer indexer = getIdentityIndexer();
                for (TraceInfo traceInfo : traces)
                    indexer.assignTraceInfo(traceInfo);
                return indexer;
            }
        }
        return super.constructIndex(mask, traces);
    }

    @Override
    public void pullInto(Collection<Tuple> collector) {
        for (Tuple t : memory) 
            collector.add(t);
    }

    public MemoryNullIndexer getNullIndexer() {
        if (memoryNullIndexer == null) {
            memoryNullIndexer = new MemoryNullIndexer(reteContainer, tupleWidth, memory.keySet(), this, this, specializedListeners);
            communicationTracker.registerDependency(this, memoryNullIndexer);
        }
        return memoryNullIndexer;
    }

    public MemoryIdentityIndexer getIdentityIndexer() {
        if (memoryIdentityIndexer == null) {
            memoryIdentityIndexer = new MemoryIdentityIndexer(reteContainer, tupleWidth, memory.keySet(), this, this, specializedListeners);
            communicationTracker.registerDependency(this, memoryIdentityIndexer);
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
