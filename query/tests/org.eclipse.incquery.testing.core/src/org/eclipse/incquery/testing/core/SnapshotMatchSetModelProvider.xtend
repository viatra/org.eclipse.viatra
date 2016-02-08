/** 
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.incquery.testing.core

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord

/** 
 */
class SnapshotMatchSetModelProvider implements IMatchSetModelProvider {
	final URI snapshotModel
	
	new(URI snapshotModel) {
		this.snapshotModel = snapshotModel
	}

	override <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet resourceSet, IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, Match filter) throws IncQueryException {
		val FQN = querySpecification.getFullyQualifiedName()
		val snapshot = resourceSet.getResource(snapshotModel, true).contents.filter(IncQuerySnapshot)
		if (snapshot.empty) throw new IllegalArgumentException(snapshotModel+" is not a Snapshot model")
		(snapshot).head?.matchSetRecords.findFirst[FQN == it.patternQualifiedName]	
	}

	override dispose() {
	}

}
