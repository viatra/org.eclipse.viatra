/** 
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core.internal

import com.google.common.base.Equivalence
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.snapshot.MatchRecord

/** 
 * @author Grill Balázs
 */
class MatchRecordEquvalence extends Equivalence<MatchRecord> {
	
	extension SnapshotHelper
	
	public static val INSTANCE = new MatchRecordEquvalence
	
	def wrap(Iterable<MatchRecord> matches){
		Sets.newHashSet(matches.map[it.wrap])
	}
	
	def unwrap(Iterable<Equivalence.Wrapper<MatchRecord>> wrapped){
		Sets.newHashSet(wrapped.map[it.get])
	}
	
	def toMap(MatchRecord record){
		val result = newHashMap()
		for(sub : record.substitutions){
			result.put(sub.parameterName, sub.derivedValue)
		}
		return result
	}
	
	/* (non-Javadoc)
	 * @see Equivalence#doEquivalent(java.lang.Object, java.lang.Object)
	 */
	override protected boolean doEquivalent(MatchRecord a, MatchRecord b) {
		Maps.difference(a.toMap, b.toMap).areEqual
	}

	/* (non-Javadoc)
	 * @see Equivalence#doHash(java.lang.Object)
	 */
	override protected int doHash(MatchRecord t) {
		t.substitutions.map[it.derivedValue?.hashCode].fold(0, [r, e | r+e])
	}
}
