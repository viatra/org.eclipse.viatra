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

package org.eclipse.viatra.query.runtime.rete.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.network.BaseNode;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * An abstract standard implementation of the Indexer interface, providing common bookkeeping functionality.
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class StandardIndexer extends BaseNode implements Indexer {

    protected Supplier parent;
    protected List<IndexerListener> listeners;
    protected TupleMask mask;

    public StandardIndexer(ReteContainer reteContainer, TupleMask mask) {
        super(reteContainer);
        this.parent = null;
        this.mask = mask;
        this.listeners = new ArrayList<IndexerListener>();
    }

    protected void propagate(Direction direction, Tuple updateElement, Tuple signature, boolean change) {
        for (IndexerListener listener : listeners) {
            listener.notifyIndexerUpdate(direction, updateElement, signature, change);
        }
    }

    /**
     * @return the mask
     */
    public TupleMask getMask() {
        return mask;
    }

    public Supplier getParent() {
        return parent;
    }

    public void attachListener(IndexerListener listener) {
        listeners.add(listener);
        reteContainer.getTracker().registerDependency(this, listener.getOwner());
    }

    public void detachListener(IndexerListener listener) {
        listeners.remove(listener);
        reteContainer.getTracker().unregisterDependency(this, listener.getOwner());
    }

    public Collection<IndexerListener> getListeners() {
        return listeners;
    }

    @Override
	public ReteContainer getContainer() {
        return reteContainer;
    }

    @Override
    protected String toStringCore() {
    	return super.toStringCore() + "(" + parent + "/" + mask + ")";
    }
    
    @Override
    public void assignTraceInfo(TraceInfo traceInfo) {
    	super.assignTraceInfo(traceInfo);
    	if (traceInfo.propagateFromIndexerToSupplierParent())
    		if (parent != null)
    			parent.acceptPropagatedTraceInfo(traceInfo);
    }

    
}
