/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.def;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.query.runtime.rete.index.DualInputNode;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.index.IndexerListener;
import org.eclipse.viatra.query.runtime.rete.index.IterableIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.def.ShapeshifterMailbox;

/**
 * Default implementation of the communication tracker. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class DefaultCommunicationTracker extends CommunicationTracker {
    
    @Override
    protected CommunicationGroup createGroup(Node representative, int index) {
        final boolean isSingleton = this.sccInformationProvider.sccs.getPartition(representative).size() == 1;
        final boolean isReceiver = representative instanceof Receiver;
        final boolean isPosetIndifferent = isReceiver
                && ((Receiver) representative).getMailbox() instanceof ShapeshifterMailbox;
        final boolean isSingletonInDRedMode = isSingleton && (representative instanceof RederivableNode)
                && ((RederivableNode) representative).isInDRedMode();

        CommunicationGroup group = null;
        // we can only use a singleton group iff
        // (1) the SCC has one node AND
        // (2) either we have a poset-indifferent mailbox OR the node is not even a receiver AND
        // (3) the node does not run in DRed mode in a singleton group
        if (isSingleton && (isPosetIndifferent || !isReceiver) && !isSingletonInDRedMode) {
            group = new SingletonCommunicationGroup(this, representative, index);
        } else {
            group = new RecursiveCommunicationGroup(this, representative, index);
        }

        return group;
    }
    
    @Override
    public Mailbox proxifyMailbox(final Node requester, final Mailbox original) {
        return original;
    }
    
    @Override
    public IndexerListener proxifyIndexerListener(final Node requester, final IndexerListener original) {
        return original;
    }
    
    @Override
    protected void processNode(final Node node) {
        if (node instanceof Receiver) {
            final Mailbox mailbox = ((Receiver) node).getMailbox();
            if (mailbox instanceof ShapeshifterMailbox) {
                final CommunicationGroup group = this.groupMap.get(node);
                final Set<Node> sccNodes = this.sccInformationProvider.sccs.getPartition(node);
                // a default mailbox must split its messages iff
                // (1) its receiver is in a recursive group and
                final boolean c1 = group.isRecursive();
                // (2) its receiver is at the SCC boundary of that group
                final boolean c2 = isAtSCCBoundary(node);
                // (3) its group consists of more than one node
                final boolean c3 = sccNodes.size() > 1;
                ((ShapeshifterMailbox) mailbox).setSplitFlag(c1 && c2 && c3);
            }
        }
    }

    /**
     * @since 2.0
     */
    private boolean isAtSCCBoundary(final Node node) {
        final CommunicationGroup ownGroup = this.groupMap.get(node);
        assert ownGroup != null;
        for (final Node source : this.dependencyGraph.getSourceNodes(node).distinctValues()) {
            final Set<Node> sourcesToCheck = new HashSet<Node>();
            sourcesToCheck.add(source);
            // DualInputNodes must be checked additionally because they do not use a mailbox directly.
            // It can happen that their indexers actually belong to other SCCs. 
            if (source instanceof DualInputNode) {
                final DualInputNode dualInput = (DualInputNode) source;
                final IterableIndexer primarySlot = dualInput.getPrimarySlot();
                if (primarySlot != null) {
                    sourcesToCheck.add(primarySlot.getActiveNode());
                }
                final Indexer secondarySlot = dualInput.getSecondarySlot();
                if (secondarySlot != null) {
                    sourcesToCheck.add(secondarySlot.getActiveNode());
                }
            }
            for (final Node current : sourcesToCheck) {
                final CommunicationGroup otherGroup = this.groupMap.get(current);
                assert otherGroup != null;
                if (!ownGroup.equals(otherGroup)) {
                    return true;
                }                
            }
        }
        return false;
    }

}
