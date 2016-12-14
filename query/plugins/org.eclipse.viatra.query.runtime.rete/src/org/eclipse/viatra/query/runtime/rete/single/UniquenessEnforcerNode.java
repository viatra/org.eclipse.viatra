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

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.index.MemoryIdentityIndexer;
import org.eclipse.viatra.query.runtime.rete.index.MemoryNullIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.network.DefaultMailbox;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;
import org.eclipse.viatra.query.runtime.rete.tuple.TupleMemory;
import org.eclipse.viatra.query.runtime.rete.util.Options;

/**
 * Ensures that no identical copies get to the output. Only one replica of each pattern substitution may traverse this
 * node.
 * 
 * @author Gabor Bergmann
 */
public class UniquenessEnforcerNode extends StandardNode implements Tunnel, RederivableNode {

    protected Collection<Supplier> parents;
    protected TupleMemory memory;
    /**
     * @since 1.6
     */
    protected TupleMemory rederivableMemory;
    /**
     * @since 1.6
     */
    protected boolean deleteRederiveEvaluation;
    protected MemoryNullIndexer memoryNullIndexer;
    protected MemoryIdentityIndexer memoryIdentityIndexer;
    protected final int tupleWidth;
    protected final Mailbox mailbox;
    private final TupleMask nullMask;
    private final TupleMask identityMask;

    public UniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth) {
        this(reteContainer, tupleWidth, false);
    }
    
    /**
     * @since 1.6
     */
    public UniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth, boolean deleteRederiveEvaluation) {
        super(reteContainer);
        this.parents = new ArrayList<Supplier>();
        this.memory = new TupleMemory();
        this.rederivableMemory = new TupleMemory();
        this.tupleWidth = tupleWidth;
        reteContainer.registerClearable(this.memory);
        reteContainer.registerClearable(this.rederivableMemory);
        this.nullMask = TupleMask.linear(0, tupleWidth);
        this.identityMask = TupleMask.identity(tupleWidth);
        this.deleteRederiveEvaluation = deleteRederiveEvaluation;
        this.mailbox = instantiateMailbox();
        reteContainer.registerClearable(this.mailbox);
    }
    
    /**
     * Instantiates the {@link Mailbox} of this receiver.
     * Subclasses may override this method to provide their own mailbox implementation.
     * 
     * @return the mailbox
     */
    protected Mailbox instantiateMailbox() {
        return new DefaultMailbox(this);
    }
    
    public TupleMemory getMemory() {
        return memory;
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }
    
    @Override
    public void update(Direction direction, Tuple update) {
        boolean propagate = false;

        if (this.deleteRederiveEvaluation) {
            if (direction == Direction.INSERT) {
                // INSERT
                boolean containedAlready = memory.demandAdd(update);

                if (!containedAlready) {
                    // memory == 0
                    if (rederivableMemory.add(update)) {
                        // the node has now a new re-derived tuple
                        reteContainer.registerRederivable(this);
                    }
                }
            } else {
                // monotonous = true/false no need to put the remaining to the rederivable
                
                // DELETE
                int memoryCount = memory.get(update);
                int rederivableCount = rederivableMemory.get(update);

                if (memoryCount > 0) {
                    if (rederivableCount != 0) {
                        issueError("[INTERNAL ERROR] Re-derivation memory for " + update
                                + " must be empty before the re-derivation phase in UniquenessEnforcer " + this
                                + " for pattern(s) " + getTraceInfoPatternsEnumerated(), null);
                    }
                    int count = memoryCount - 1;
                    if (count > 0) {
                        // the node still has re-derivable tuple
                        reteContainer.registerRederivable(this);
                        rederivableMemory.add(update, count);
                    }
                    memory.clear(update);
                    propagate = true;
                } else {
                    // memory == 0
                    try {
                        rederivableMemory.remove(update);
                    } catch (NullPointerException ex) {
                        issueError("[INTERNAL ERROR] Duplicate deletion of " + update
                                + " was detected in UniquenessEnforcer " + this + " for pattern(s) "
                                + getTraceInfoPatternsEnumerated(), ex);
                    }
                    if (rederivableMemory.size() == 0) {
                        // the tuple lost all of its derivations
                        reteContainer.unregisterRederivable(this);
                    }
                }
            }
        } else {
            if (direction == Direction.INSERT) {
                // INSERT
                propagate = this.memory.add(update);
            } else {
                // DELETE
                try {
                    propagate = this.memory.remove(update);
                } catch (NullPointerException ex) {
                    propagate = false;
                    issueError(
                            "[INTERNAL ERROR] Duplicate deletion of " + update + " was detected in UniquenessEnforcer "
                                    + this + " for pattern(s) " + getTraceInfoPatternsEnumerated(),
                            ex);
                }
            }
        }

        if (propagate) {
            propagate(direction, update);
        }
    }

    private void issueError(String message, Exception ex) {
        if (ex == null) {
            this.reteContainer.getNetwork().getEngine().getLogger().error(message);
        } else {
            this.reteContainer.getNetwork().getEngine().getLogger().error(message, ex);
        }
    }

    /**
     * @since 1.6
     */
    @Override
    public void rederiveOne() {
        Tuple update = rederivableMemory.iterator().next();
        int count = rederivableMemory.get(update);
        rederivableMemory.clear(update);
        memory.add(update, count);
        if (this.rederivableMemory.size() == 0) {
            reteContainer.unregisterRederivable(this);
        }
        propagate(Direction.INSERT, update);
    }

    /**
     * @since 1.6
     */
    protected void propagate(Direction direction, Tuple update) {
        propagateUpdate(direction, update);

        if (memoryIdentityIndexer != null) {
            memoryIdentityIndexer.propagate(direction, update);
        }
        if (memoryNullIndexer != null) {
            memoryNullIndexer.propagate(direction, update);
        }
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
        collector.addAll(memory);
    }

    public MemoryNullIndexer getNullIndexer() {
        if (memoryNullIndexer == null)
            memoryNullIndexer = new MemoryNullIndexer(reteContainer, tupleWidth, memory, this, this);
        return memoryNullIndexer;
    }

    public MemoryIdentityIndexer getIdentityIndexer() {
        if (memoryIdentityIndexer == null)
            memoryIdentityIndexer = new MemoryIdentityIndexer(reteContainer, tupleWidth, memory, this, this);
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

}
