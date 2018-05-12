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

import java.util.Collections;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * @author Tamas Szabo
 * @since 2.0
 */
public class DefaultMessageIndexer implements MessageIndexer {

	protected final Map<Tuple, Integer> indexer;

	public DefaultMessageIndexer() {
		this.indexer = CollectionsFactory.createMap();
	}

	public Map<Tuple, Integer> getTuples() {
		return Collections.unmodifiableMap(this.indexer);
	}

	@Override
	public int getCount(final Tuple update) {
		final Integer count = getTuples().get(update);
		if (count == null) {
			return 0;
		} else {
			return count;
		}
	}

	@Override
	public void insert(final Tuple update) {
		update(update, 1);
	}

	@Override
	public void delete(final Tuple update) {
		update(update, -1);
	}

	@Override
	public void update(final Tuple update, final int delta) {
		final Integer oldCount = this.indexer.get(update);
		final int newCount = (oldCount == null ? 0 : oldCount) + delta;
		if (newCount == 0) {
			this.indexer.remove(update);
		} else {
			this.indexer.put(update, newCount);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.indexer.isEmpty();
	}

	@Override
	public void clear() {
		this.indexer.clear();
	}

}
