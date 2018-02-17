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
package org.eclipse.viatra.query.runtime.rete.network.indexer;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * A message indexer is used by {@link Mailbox}es to index their contents. 
 * 
 * @author Tamas Szabo
 */
public interface MessageIndexer extends Clearable {
	
	public void insert(final Tuple update);

	public void delete(final Tuple update);

	public void update(final Tuple update, final int delta);
	
	public boolean isEmpty();
	
	public int getCount(final Tuple update);

}
