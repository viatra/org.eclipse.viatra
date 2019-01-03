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

package org.eclipse.viatra.query.runtime.rete.remote;

import java.util.Collection;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.single.SingleInputNode;

/**
 * This node receives updates from a remote supplier; no local updates are expected.
 * 
 * @author Gabor Bergmann
 * 
 */
public class RemoteSupplier extends SingleInputNode {

    RemoteReceiver counterpart;

    public RemoteSupplier(ReteContainer reteContainer, RemoteReceiver counterpart) {
        super(reteContainer);
        this.counterpart = counterpart;
        counterpart.addTarget(reteContainer.makeAddress(this));
    }

    @Override
    public void pullInto(Collection<Tuple> collector, boolean flush) {
        Collection<Tuple> pulled = counterpart.remotePull(flush);
        collector.addAll(pulled);
    }

    @Override
    public void pullIntoWithTimestamp(Map<Tuple, DifferentialTimestamp> collector, boolean flush) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void update(Direction direction, Tuple updateElement, DifferentialTimestamp timestamp) {
        propagateUpdate(direction, updateElement, timestamp);
    }

}
