/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.mailbox;

import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;

/**
 * A fall through mailbox directly calls the update method of its {@link Receiver} if fall through is enabled. 
 * The fall through flag is controlled by the {@link CommunicationTracker} based on the receiver node type and network topology.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public interface FallThroughMailbox extends Mailbox {

    public boolean isFallThrough();

    public void setFallThrough(final boolean fallThrough);
    
}
