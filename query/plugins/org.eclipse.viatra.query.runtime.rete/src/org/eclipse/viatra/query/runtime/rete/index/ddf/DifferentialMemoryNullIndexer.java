/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.index.ddf;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.index.def.DefaultMemoryNullIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;

public class DifferentialMemoryNullIndexer extends DefaultMemoryNullIndexer {

    protected final Map<Tuple, DifferentialTimestamp> memoryWithTimestamp;

    public DifferentialMemoryNullIndexer(final ReteContainer reteContainer, final int tupleWidth,
            final Map<Tuple, DifferentialTimestamp> memoryWithTimestamp, final Supplier parent,
            final Receiver activeNode, final List<ListenerSubscription> sharedSubscriptionList) {
        super(reteContainer, tupleWidth, memoryWithTimestamp.keySet(), parent, activeNode, sharedSubscriptionList);
        this.memoryWithTimestamp = memoryWithTimestamp;
    }

    @Override
    public Map<Tuple, DifferentialTimestamp> getWithTimestamp(final Tuple signature) {
        if (nullSignature.equals(signature)) {
            return isEmpty() ? null : this.memoryWithTimestamp;
        } else {
            return null;
        }
    }

}
