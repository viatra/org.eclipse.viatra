/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
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
