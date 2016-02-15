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

import java.util.Set
import org.eclipse.viatra.query.testing.snapshot.MatchRecord
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord
import com.google.common.collect.Sets

/** 
 * Difference descriptor for {@link MatchSetRecord} elements.
 */
class MatchSetRecordDiff {
	
	new(Set<MatchRecord> additions, Set<MatchRecord> removals){
		this.additions = additions;
		this.removals = removals;
	}
	
	private val Set<MatchRecord> additions
	private val Set<MatchRecord> removals

	def Set<MatchRecord> getAdditions() {
		return additions
	}

	def Set<MatchRecord> getRemovals() {
		return removals
	}
	
	def boolean isEmpty(){
		additions.empty && removals.empty
	}

	def static compute(MatchSetRecord expected, MatchSetRecord actual) {
		val expectedSet = MatchRecordEquvalence::INSTANCE.wrap(expected.matches)
		val actualSet = MatchRecordEquvalence::INSTANCE.wrap(actual.matches)
		
		val unexpected = Sets::difference(actualSet, expectedSet)
		val missing = Sets::difference(expectedSet, actualSet)
		
		new MatchSetRecordDiff(
			MatchRecordEquvalence::INSTANCE.unwrap(unexpected),
			MatchRecordEquvalence::INSTANCE.unwrap(missing)
		)
	}
}
