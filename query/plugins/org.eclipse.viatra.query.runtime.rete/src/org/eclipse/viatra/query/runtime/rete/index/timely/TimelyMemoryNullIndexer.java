/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.index.timely;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.TimelyMemory;
import org.eclipse.viatra.query.runtime.matchers.util.timeline.Timeline;
import org.eclipse.viatra.query.runtime.rete.index.NullIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;

public class TimelyMemoryNullIndexer extends NullIndexer {

    protected final TimelyMemory<Timestamp> memory;

    public TimelyMemoryNullIndexer(final ReteContainer reteContainer, final int tupleWidth,
            final TimelyMemory<Timestamp> memory, final Supplier parent,
            final Receiver activeNode, final List<ListenerSubscription> sharedSubscriptionList) {
        super(reteContainer, tupleWidth, parent, activeNode, sharedSubscriptionList);
        this.memory = memory;
    }

    @Override
    public Map<Tuple, Timeline<Timestamp>> getTimeline(final Tuple signature) {
        if (nullSignature.equals(signature)) {
            return isEmpty() ? null : this.memory.asMap();
        } else {
            return null;
        }
    }
    
    @Override
    protected Collection<Tuple> getTuples() {
        return this.memory.getTuplesAtInfinity();
    }

}
