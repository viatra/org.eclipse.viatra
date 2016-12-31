/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.single;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.DefaultMailbox;
import org.eclipse.viatra.query.runtime.rete.network.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class SingleInputNode extends StandardNode implements Tunnel {

    protected Supplier parent;
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

    /**
     * To be called by derived classes and ReteContainer.
     */
    public void propagatePullInto(Collection<Tuple> collector) {
        if (parent != null)
            parent.pullInto(collector);
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
