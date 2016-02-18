/** 
 * Copyright (c) 2010-2015, Grill Bal�zs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Bal�zs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot

/** 
 */
class SnapshotMatchSetModelProvider implements IMatchSetModelProvider {
	final URI snapshotModel
	
	new(URI snapshotModel) {
		this.snapshotModel = snapshotModel
	}

	override <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet resourceSet, IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter) throws ViatraQueryException {
		val FQN = querySpecification.getFullyQualifiedName()
		val snapshot = resourceSet.getResource(snapshotModel, true).contents.filter(QuerySnapshot)
		if (snapshot.empty) throw new IllegalArgumentException(snapshotModel+" is not a Snapshot model")
		(snapshot).head?.matchSetRecords.findFirst[FQN == it.patternQualifiedName]	
	}

	override dispose() {
	}

}
