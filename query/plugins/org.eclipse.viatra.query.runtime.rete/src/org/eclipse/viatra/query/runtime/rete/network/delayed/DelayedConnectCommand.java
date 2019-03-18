/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.delayed;

import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;

public class DelayedConnectCommand extends DelayedCommand {

    public DelayedConnectCommand(final Supplier supplier, final Receiver receiver, final ReteContainer container) {
        super(supplier, receiver, Direction.INSERT, container);
    }

    @Override
    protected boolean isTimestampAware() {
        return this.container.isDifferentialDataFlowEvaluation() && this.container.getCommunicationTracker().areInSameGroup(this.supplier, this.receiver);
    }

}
