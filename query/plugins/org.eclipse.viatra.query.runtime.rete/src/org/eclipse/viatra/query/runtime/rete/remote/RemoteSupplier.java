/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.remote;

import java.util.Collection;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.Direction;
import org.eclipse.viatra.query.runtime.matchers.util.timeline.Timeline;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
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
    public void pullIntoWithTimeline(Map<Tuple, Timeline<Timestamp>> collector, boolean flush) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void update(Direction direction, Tuple updateElement, Timestamp timestamp) {
        propagateUpdate(direction, updateElement, timestamp);
    }

}
