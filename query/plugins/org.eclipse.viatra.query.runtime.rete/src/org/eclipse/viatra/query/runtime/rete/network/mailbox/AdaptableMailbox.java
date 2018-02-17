/*******************************************************************************
 * Copyright (c) 2010-2018, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.mailbox;

import org.eclipse.viatra.query.runtime.rete.network.CommunicationTracker;

/**
 * An adaptable mailbox can be wrapped by another mailbox to act in behalf of that. The significance of the adaptation
 * is that the adaptee will notify the {@link CommunicationTracker} about updates by promoting the adapter itself.
 * Adaptable mailboxes are used by the {@link AdaptiveMailbox}.
 * 
 * @author Tamas Szabo
 */
public interface AdaptableMailbox extends Mailbox {

    public Mailbox getAdapter();

    public void setAdapter(final Mailbox adapter);

}
