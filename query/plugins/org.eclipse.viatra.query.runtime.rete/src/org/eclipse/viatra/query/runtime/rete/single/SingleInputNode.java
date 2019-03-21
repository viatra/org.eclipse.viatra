/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.single;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.timeless.BehaviorChangingMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.timely.TimelyMailbox;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class SingleInputNode extends StandardNode implements Tunnel {

    protected Supplier parent;
    /**
     * @since 1.6
     */
    protected Mailbox mailbox;
    
    public SingleInputNode(ReteContainer reteContainer) {
        super(reteContainer);
        mailbox = instantiateMailbox();
        reteContainer.registerClearable(mailbox);
        parent = null;
    }
    
    /**
     * Instantiates the {@link Mailbox} of this receiver.
     * Subclasses may override this method to provide their own mailbox implementation.
     * 
     * @return the mailbox
     * @since 2.0
     */
    protected Mailbox instantiateMailbox() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()) {
            return new TimelyMailbox(this, this.reteContainer);
        } else {            
            return new BehaviorChangingMailbox(this, this.reteContainer);
        }
    }
    
    @Override
    public CommunicationTracker getCommunicationTracker() {
        return this.reteContainer.getCommunicationTracker();
    }
    
    @Override
    public Mailbox getMailbox() {
        return this.mailbox;
    }

    @Override
    public void appendParent(Supplier supplier) {
        if (parent == null)
            parent = supplier;
        else
            throw new UnsupportedOperationException("Illegal RETE edge: " + this + " already has a parent (" + parent
                    + ") and cannot connect to additional parent (" + supplier
                    + ") as it is not a Uniqueness Enforcer Node. ");
    }

    @Override
    public void removeParent(Supplier supplier) {
        if (parent == supplier)
            parent = null;
        else
            throw new IllegalArgumentException("Illegal RETE edge removal: the parent of " + this + " is not "
                    + supplier);
    }

    /**
     * To be called by derived classes and ReteContainer.
     */
    public void propagatePullInto(final Collection<Tuple> collector, final boolean flush) {
        if (parent != null) {
            parent.pullInto(collector, flush);
        }
    }
    
    /**
     * To be called by derived classes and ReteContainer.
     */
    public void propagatePullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
        if (parent != null) {
            parent.pullIntoWithTimestamp(collector, flush);
        }
    }

    @Override
    public Collection<Supplier> getParents() {
        if (parent == null)
            return Collections.emptySet();
        else
            return Collections.singleton(parent);
    }
    
    @Override
    public void assignTraceInfo(TraceInfo traceInfo) {
        super.assignTraceInfo(traceInfo);
        if (traceInfo.propagateFromStandardNodeToSupplierParent())
            if (parent != null)
                parent.acceptPropagatedTraceInfo(traceInfo);
    }

}
