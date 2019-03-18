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
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;

public abstract class TransformerNode extends SingleInputNode {

    public TransformerNode(final ReteContainer reteContainer) {
        super(reteContainer);
    }

    protected abstract Tuple transform(final Tuple input);

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        for (Tuple ps : reteContainer.pullPropagatedContents(this, flush)) {
            collector.add(transform(ps));
        }
    }
    
    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector, final boolean flush) {
        for (final Entry<Tuple, DifferentialTimestamp> entry : reteContainer.pullPropagatedContentsWithTimestamp(this, flush).entrySet()) {
            collector.put(transform(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void update(final Direction direction, final Tuple updateElement, final DifferentialTimestamp timestamp) {
        propagateUpdate(direction, transform(updateElement), timestamp);
    }

}
