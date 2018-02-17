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
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * @author Tamas Szabo
 */
public class GroupBasedMessageIndexer implements MessageIndexer {

	protected final Map<Tuple, DefaultMessageIndexer> indexer;
	protected final TupleMask groupMask;

	public GroupBasedMessageIndexer(final TupleMask groupMask) {
		this.indexer = CollectionsFactory.createMap();
		this.groupMask = groupMask;
	}

	public Map<Tuple, Integer> getTuplesByGroup(final Tuple group) {
		final DefaultMessageIndexer values = this.indexer.get(group);
		if (values == null) {
			return Collections.emptyMap();
		} else {
			return Collections.unmodifiableMap(values.getTuples());
		}
	}

	public int getCount(final Tuple update) {
		final Tuple group = this.groupMask.transform(update);
		final Integer count = getTuplesByGroup(group).get(update);
		if (count == null) {
			return 0;
		} else {
			return count;
		}
	}

	public Set<Tuple> getGroups() {
		return Collections.unmodifiableSet(this.indexer.keySet());
	}

	public void insert(final Tuple update) {
		update(update, 1);
	}

	public void delete(final Tuple update) {
		update(update, -1);
	}

	public void update(final Tuple update, final int delta) {
		final Tuple group = this.groupMask.transform(update);
		DefaultMessageIndexer valueIndexer = this.indexer.get(group);

		if (valueIndexer == null) {
			valueIndexer = new DefaultMessageIndexer();
			this.indexer.put(group, valueIndexer);
		}

		valueIndexer.update(update, delta);

		// it may happen that the indexer becomes empty as a result of the update
		if (valueIndexer.isEmpty()) {
			this.indexer.remove(group);
		}
	}

	public boolean isEmpty() {
		return this.indexer.isEmpty();
	}

	@Override
	public void clear() {
		this.indexer.clear();
	}

}
