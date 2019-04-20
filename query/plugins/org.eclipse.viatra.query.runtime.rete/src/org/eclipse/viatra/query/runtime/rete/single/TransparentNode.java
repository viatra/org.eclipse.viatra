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
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.Direction;
import org.eclipse.viatra.query.runtime.matchers.util.timeline.Timeline;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;

/**
 * Simply propagates everything. Might be used to join or fork.
 * 
 * @author Gabor Bergmann
 */
public class TransparentNode extends SingleInputNode {

    public TransparentNode(final ReteContainer reteContainer) {
        super(reteContainer);
    }

    @Override
    public void update(final Direction direction, final Tuple updateElement, final Timestamp timestamp) {
        propagateUpdate(direction, updateElement, timestamp);

    }

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        propagatePullInto(collector, flush);
    }

    @Override
    public void pullIntoWithTimeline(final Map<Tuple, Timeline<Timestamp>> collector, final boolean flush) {
        propagatePullIntoWithTimestamp(collector, flush);
    }
    
}
