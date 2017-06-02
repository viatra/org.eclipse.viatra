/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.misc;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.viatra.query.runtime.rete.network.BaseNode;
import org.eclipse.viatra.query.runtime.rete.network.DefaultMailbox;
import org.eclipse.viatra.query.runtime.rete.network.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * @author Bergmann Gabor
 * 
 */
public abstract class SimpleReceiver extends BaseNode implements Receiver {

    protected Supplier parent = null;
    /**
     * @since 1.6
     */
    protected final Mailbox mailbox;
    
    /**
     * @param reteContainer
     */
    public SimpleReceiver(ReteContainer reteContainer) {
        super(reteContainer);
        mailbox = instantiateMailbox();
        reteContainer.registerClearable(mailbox);
    }

    /**
     * Instantiates the {@link Mailbox} of this receiver.
     * Subclasses may override this method to provide their own mailbox implementation.
     * 
     * @return the mailbox
     * @since 1.6
     */
    protected Mailbox instantiateMailbox() {
        return new DefaultMailbox(this, this.reteContainer);
    }
    
    @Override
    public Mailbox getMailbox() {
        return mailbox;
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

    @Override
    public Collection<Supplier> getParents() {
        if (parent == null)
            return Collections.emptySet();
        else
            return Collections.singleton(parent);
    }

    /**
     * Disconnects this node from the network. Can be called publicly.
     * 
     * @pre: child nodes, if any, must already be disconnected.
     */
    public void disconnectFromNetwork() {
        if (parent != null)
            reteContainer.disconnect(parent, this);
    }
    
    @Override
    public void assignTraceInfo(TraceInfo traceInfo) {
        super.assignTraceInfo(traceInfo);
        if (traceInfo.propagateFromStandardNodeToSupplierParent())
            if (parent != null)
                parent.acceptPropagatedTraceInfo(traceInfo);
    }

}