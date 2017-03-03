/** 
 * Copyright (c) 2010-2015, Grill Balazs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balazs - initial API and implementation
 * Peter Lunk - EMFScope support added
 */
package org.eclipse.viatra.query.testing.core

import com.google.common.collect.Sets
import java.util.Set
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot

/** 
 */
class SnapshotMatchSetModelProvider implements IMatchSetModelProvider {
    final URI snapshotModel

    new(URI snapshotModel) {
        this.snapshotModel = snapshotModel
    }

    override <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(EMFScope scope,
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
        Match filter) throws ViatraQueryException {
        val FQN = querySpecification.getFullyQualifiedName()

        val Set<QuerySnapshot> snapshot = Sets.newHashSet;
        for (Notifier n : scope.scopeRoots) {
            switch (n) {
                ResourceSet: snapshot.addAll(n.getResource(snapshotModel, true).contents.filter(QuerySnapshot))
                Resource: if(n.URI.equals(snapshotModel)) snapshot.addAll(n.contents.filter(QuerySnapshot))
            }
        }

        return getMatchSetRecordsFromSnapshot(snapshot, FQN)
    }

    override updatedByModify() {
        false
    }

    override <Match extends IPatternMatch> getMatchSetRecord(ResourceSet resourceSet,
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
        Match filter) throws ViatraQueryException {
        val FQN = querySpecification.getFullyQualifiedName()
        val snapshot = resourceSet.getResource(snapshotModel, true).contents.filter(QuerySnapshot)
        return getMatchSetRecordsFromSnapshot(snapshot, FQN)
    }

    override dispose() {}

    private def <Match extends IPatternMatch> MatchSetRecord getMatchSetRecordsFromSnapshot(
        Iterable<QuerySnapshot> snapshot, String FQN) {
        if(snapshot.empty) throw new IllegalArgumentException(snapshotModel + " is not a Snapshot model")
        val record = (snapshot).head?.matchSetRecords.findFirst[FQN == it.patternQualifiedName]
        if (record == null)
            throw new IllegalArgumentException("Could not find snapshot for " + FQN + " in " + snapshotModel)
        record
    }

}