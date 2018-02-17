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

import org.eclipse.viatra.query.runtime.rete.network.indexer.MessageIndexer;

/**
 * A factory used to create message indexers for {@link Mailbox}es.
 * 
 * @author Tamas Szabo
 */
public interface MessageIndexerFactory<I extends MessageIndexer> {

    public I create();

}
